#!/usr/bin/env ruby
# Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

# A workflow tool for JRuby+Truffle development

# Recommended: function jt { ruby tool/jt.rb $@; }

require 'fileutils'
require 'digest/sha1'

JRUBY_DIR = File.expand_path('../..', __FILE__)

# wait for sub-processes to handle the interrupt
trap(:INT) {}

module Utilities

  def self.graal_version
    File.foreach("#{JRUBY_DIR}/truffle/pom.rb") do |line|
      if /jar 'com.oracle:truffle:(\d+\.\d+(?:-SNAPSHOT)?)'/ =~ line
        break $1
      end
    end
  end

  def self.find_graal
    base_graal_path = if graal_version.include?('SNAPSHOT')
      'basic-graal/jdk1.8.0_05/product'
    else
      'graalvm-jdk1.8.0'
    end

    graal_locations = [
      ENV["GRAAL_BIN_#{mangle_for_env(git_branch)}"],
      ENV['GRAAL_BIN'],
      "#{base_graal_path}/bin/java",
      "../#{base_graal_path}/bin/java",
      "../../#{base_graal_path}/bin/java",
    ].compact.map { |path| File.expand_path(path, JRUBY_DIR) }

    not_found = -> {
      raise "couldn't find graal - download it from http://lafo.ssw.uni-linz.ac.at/graalvm/ and extract it into the JRuby repository or parent directory"
    }

    graal_locations.find(not_found) do |location|
      File.executable?(location)
    end
  end

  def self.git_branch
    @git_branch ||= `git rev-parse --abbrev-ref HEAD`.strip
  end

  def self.mangle_for_env(name)
    name.upcase.tr('-', '_')
  end

  def self.find_graal_mx
    mx = File.expand_path('../../../../mx.sh', find_graal)
    raise "couldn't find mx.sh - set GRAAL_BIN, and you need to use a checkout of Graal, not a build" unless File.executable?(mx)
    mx
  end

  def self.igv_running?
    `ps`.lines.any? { |p| p.include? 'mxtool/mx.py igv' }
  end

  def self.ensure_igv_running
    unless igv_running?
      spawn "#{find_graal_mx} igv", pgroup: true
#      sleep 5
      puts
      puts
      puts "-------------"
      puts "Waiting for IGV start"
      puts "The first time you run IGV it may take several minutes to download dependencies and compile"
      puts "Press enter when you see the IGV window"
      puts "-------------"
      puts
      puts
      $stdin.gets
    end
  end

  def self.find_bench
    bench_locations = [
      ENV['BENCH_DIR'],
      'bench9000',
      '../bench9000'
    ].compact.map { |path| File.expand_path(path, JRUBY_DIR) }

    not_found = -> {
      raise "couldn't find bench9000 - clone it from https://github.com/jruby/bench9000.git into the JRuby repository or parent directory"
    }

    bench_locations.find(not_found) do |location|
      Dir.exist?(location)
    end
  end

  def self.jruby_version
    File.read("#{JRUBY_DIR}/VERSION").strip
  end

end

module ShellUtils
  private

  def raw_sh(*args)
    result = system(*args)
    unless result
      $stderr.puts "FAILED (#{$?}): #{args * ' '}"
      exit $?.exitstatus
    end
  end

  def sh(*args)
    Dir.chdir(JRUBY_DIR) do
      raw_sh(*args)
    end
  end

  def mvn(*args)
    sh 'mvn', *args
  end

  def mspec(command, *args)
    sh 'ruby', 'spec/mspec/bin/mspec', command, '--config', 'spec/truffle/truffle.mspec', *args
  end
end

module Commands
  include ShellUtils

  def help
    puts 'jt build                                     build'
    puts 'jt build truffle                             build only the Truffle part, assumes the rest is up-to-date'
    puts 'jt clean                                     clean'
    puts 'jt rebuild                                   clean and build'
    puts 'jt run [options] args...                     run JRuby with -X+T and args'
    puts '    --graal        use Graal (set GRAAL_BIN or it will try to automagically find it)'
    puts '    --asm          show assembly (implies --graal)'
    puts '    --server       run an instrumentation server on port 8080'
    puts '    --igv          make sure IGV is running and dump Graal graphs after partial escape (implies --graal)'
    puts 'jt test mri                                  run mri tests'
    puts 'jt test                                      run all specs'
    puts 'jt test fast                                 run all specs except sub-processes, GC, sleep, ...'
    puts 'jt test spec/ruby/language                   run specs in this directory'
    puts 'jt test spec/ruby/language/while_spec.rb     run specs in this file'
    puts 'jt test pe                                   run partial evaluation tests'
    puts 'jt tag spec/ruby/language                    tag failing specs in this directory'
    puts 'jt tag spec/ruby/language/while_spec.rb      tag failing specs in this file'
    puts 'jt tag all spec/ruby/language                tag all specs in this file, without running them'
    puts 'jt untag spec/ruby/language                  untag passing specs in this directory'
    puts 'jt untag spec/ruby/language/while_spec.rb    untag passing specs in this file'
    puts 'jt bench debug benchmark                     run a single benchmark with options for compiler debugging'
    puts 'jt bench reference [benchmarks]              run a set of benchmarks and record a reference point'
    puts 'jt bench compare [benchmarks]                run a set of benchmarks and compare against a reference point'
    puts '    benchmarks can be any benchmarks of group of benchmarks supported'
    puts '    by bench9000, eg all, classic, chunky, 3, 5, 10, 15 - default is 5'
    puts 'jt findbugs                                  run findbugs'
    puts 'jt findbugs report                           run findbugs and generate an HTML report'
    puts 'jt install ..../graal/mx/suite.py            install a JRuby distribution into an mx suite'
    puts
    puts 'you can also put build or rebuild in front of any command'
    puts
    puts 'recognised environment variables:'
    puts
    puts '  GRAAL_BIN                                  GraalVM executable (java command) to use'
    puts '  GRAAL_BIN_...git_branch_name...            GraalVM executable to use for a given branch'
    puts '           branch names are mangled - eg truffle-head becomes GRAAL_BIN_TRUFFLE_HEAD'
  end

  def build(project = nil)
    case project
    when 'truffle'
      mvn '-pl', 'truffle', 'package'
    when nil
      mvn 'package'
    else
      raise ArgumentError, project
    end
  end

  def clean
    mvn 'clean'
  end

  def rebuild
    clean
    build
  end

  def run(*args)
    env_vars = {}
    jruby_args = %w[-X+T]

    { '--asm' => '--graal', '--igv' => '--graal' }.each_pair do |arg, dep|
      args.unshift dep if args.include?(arg)
    end

    if args.delete('--graal')
      env_vars["JAVACMD"] = Utilities.find_graal
      jruby_args << '-J-server'
    end

    if args.delete('--asm')
      jruby_args += %w[-J-XX:+UnlockDiagnosticVMOptions -J-XX:CompileCommand=print,*::callRoot]
    end

    if args.delete('--jdebug')
      jruby_args += %w[-J-agentlib:jdwp=transport=dt_socket,server=y,address=8000,suspend=y]
    end

    if args.delete('--server')
      jruby_args += %w[-Xtruffle.instrumentation_server_port=8080 -Xtruffle.passalot=1]
    end

    if args.delete('--igv')
      raise "--igv doesn't work on master - you need a branch that builds against latest graal" if Utilities.git_branch == 'master'
      Utilities.ensure_igv_running
      jruby_args += %w[-J-G:Dump=TrufflePartialEscape]
      #jruby_args += %w[-J-G:Dump=Truffle]
    end
    if args.delete('--inline')
      jruby_args += %w[-J-G:+TraceTruffleInlining]
    end

    raw_sh(env_vars, "#{JRUBY_DIR}/bin/jruby", *jruby_args, *args)
  end
  alias ruby run

  def test_mri(*args)
    env_vars = {}
    jruby_args = %w[-X+T]

    if args.empty?
      args += %w[ruby/test_alias.rb ruby/test_array.rb ruby/test_assignment.rb ruby/test_autoload.rb
                 ruby/test_basicinstructions.rb ruby/test_beginendblock.rb ruby/test_bignum.rb ruby/test_call.rb ruby/test_case.rb ruby/test_class.rb
                 ruby/test_clone.rb ruby/test_comparable.rb ruby/test_complex.rb ruby/test_complex2.rb ruby/test_complexrational.rb ruby/test_condition.rb
                 ruby/test_const.rb ruby/test_continuation.rb ruby/test_defined.rb ruby/test_dir.rb ruby/test_dir_m17n.rb ruby/test_econv.rb ruby/test_encoding.rb
                 ruby/test_enum.rb ruby/test_enumerator.rb ruby/test_env.rb ruby/test_eval.rb ruby/test_exception.rb ruby/test_file.rb ruby/test_file_exhaustive.rb
                 ruby/test_fixnum.rb ruby/test_flip.rb ruby/test_float.rb ruby/test_fnmatch.rb ruby/test_gc.rb ruby/test_hash.rb ruby/test_ifunless.rb ruby/test_integer.rb
                 ruby/test_integer_comb.rb ruby/test_io.rb ruby/test_io_m17n.rb ruby/test_iterator.rb ruby/test_lambda.rb ruby/test_literal.rb ruby/test_m17n.rb ruby/test_m17n_comb.rb
                 ruby/test_marshal.rb ruby/test_math.rb ruby/test_metaclass.rb ruby/test_method.rb ruby/test_mixed_unicode_escapes.rb ruby/test_module.rb ruby/test_notimp.rb ruby/test_numeric.rb
                 ruby/test_object.rb ruby/test_objectspace.rb ruby/test_optimization.rb ruby/test_pack.rb ruby/test_parse.rb ruby/test_path.rb ruby/test_pipe.rb
                 ruby/test_primitive.rb ruby/test_process.rb ruby/test_rand.rb ruby/test_range.rb ruby/test_rational.rb ruby/test_rational2.rb ruby/test_readpartial.rb ruby/test_regexp.rb
                 ruby/test_require.rb ruby/test_rubyoptions.rb ruby/test_signal.rb ruby/test_sleep.rb ruby/test_sprintf.rb ruby/test_string.rb ruby/test_stringchar.rb ruby/test_struct.rb
                 ruby/test_super.rb ruby/test_symbol.rb ruby/test_syntax.rb ruby/test_system.rb ruby/test_thread.rb ruby/test_time.rb ruby/test_time_tz.rb ruby/test_trace.rb
                 ruby/test_transcode.rb ruby/test_undef.rb ruby/test_unicode_escape.rb ruby/test_variable.rb ruby/test_whileuntil.rb ruby/test_yield.rb test_pp.rb
                 test_cmath.rb test_delegate.rb test_find.rb test_ipaddr.rb test_open3.rb test_pp.rb test_prettyprint.rb test_prime.rb test_pstore.rb test_pty.rb test_securerandom.rb
                 test_set.rb test_shellwords.rb test_singleton.rb test_syslog.rb test_tempfile.rb test_time.rb test_timeout.rb test_tsort.rb base64/test_base64.rb benchmark/test_benchmark.rb
                 bigdecimal/test_bigdecimal.rb bigdecimal/test_bigdecimal_util.rb bigdecimal/test_bigmath.rb cgi/test_cgi_cookie.rb cgi/test_cgi_core.rb cgi/test_cgi_header.rb
                 cgi/test_cgi_modruby.rb cgi/test_cgi_multipart.rb cgi/test_cgi_session.rb cgi/test_cgi_tag_helper.rb cgi/test_cgi_util.rb coverage/test_coverage.rb csv/test_csv_parsing.rb
                 csv/test_csv_writing.rb csv/test_data_converters.rb csv/test_encodings.rb csv/test_features.rb csv/test_headers.rb csv/test_interface.rb csv/test_row.rb csv/test_table.rb
                 date/test_date.rb date/test_date_arith.rb date/test_date_attr.rb date/test_date_base.rb date/test_date_compat.rb date/test_date_conv.rb date/test_date_marshal.rb
                 date/test_date_new.rb date/test_date_parse.rb date/test_date_strftime.rb date/test_date_strptime.rb date/test_date_parse.rb date/test_switch_hitter.rb
                 digest/test_digest.rb digest/test_digest_extend.rb erb/test_erb.rb erb/test_erb_m17n.rb etc/test_etc.rb fileutils/test_dryrun.rb fileutils/test_fileutils.rb
                 fileutils/test_nowrite.rb fileutils/test_verbose.rb io/wait/test_io_wait.rb io/nonblock/test_flush.rb io/console/test_io_console.rb json/test_json.rb
                 json/test_json_addition.rb json/test_json_encoding.rb json/test_json_fixtures.rb json/test_json_generate.rb json/test_json_generic_object.rb
                 json/test_json_string_matching.rb json/test_json_unicode.rb logger/test_logger.rb matrix/test_matrix.rb matrix/test_vector.rb monitor/test_monitor.rb
                 net/ftp/test_ftp.rb net/http/test_buffered_io.rb net/http/test_http.rb net/http/test_http_request.rb net/http/test_httpheader.rb net/http/test_httpresponse.rb
                 net/http/test_httpresponses.rb net/http/test_https.rb net/http/test_https_proxy.rb net/imap/test_imap_response_parser.rb net/pop/test_pop.rb net/protocol/test_protocol.rb
                 net/smtp/test_response.rb net/smtp/test_smtp.rb net/smtp/test_ssl_socket.rb nkf/test_kconv.rb nkf/test_nkf.rb optparse/test_bash_completion.rb optparse/test_getopts.rb
                 optparse/test_noarg.rb optparse/test_optarg.rb optparse/test_optparse.rb optparse/test_placearg.rb optparse/test_reqarg.rb optparse/test_summary.rb
                 optparse/test_zsh_completion.rb ostruct/test_ostruct.rb pathname/test_pathname.rb psych/test_alias_and_anchor.rb psych/test_array.rb psych/test_boolean.rb
                 psych/test_class.rb psych/test_coder.rb psych/test_date_time.rb psych/test_deprecated.rb psych/test_document.rb psych/test_emitter.rb psych/test_encoding.rb
                 psych/test_exception.rb psych/test_hash.rb psych/test_json_tree.rb psych/test_merge_keys.rb psych/test_nil.rb psych/test_null.rb psych/test_numeric.rb
                 psych/test_object.rb psych/test_object_references.rb psych/test_omap.rb psych/test_parser.rb psych/test_psych.rb psych/test_scalar.rb psych/test_scalar_scanner.rb
                 psych/test_serialize_subclasses.rb psych/test_set.rb psych/test_stream.rb psych/test_string.rb psych/test_struct.rb psych/test_symbol.rb
                 psych/test_tainted.rb psych/test_to_yaml_properties.rb psych/test_tree_builder.rb psych/test_yaml.rb psych/test_yamlstore.rb psych/json/test_stream.rb
                 psych/nodes/test_enumerable.rb psych/visitors/test_depth_first.rb psych/visitors/test_emitter.rb psych/visitors/test_to_ruby.rb psych/visitors/test_yaml_tree.rb
                 ripper/test_files.rb ripper/test_filter.rb ripper/test_parser_events.rb ripper/test_ripper.rb ripper/test_scanner_events.rb scanf/test_scanf.rb
                 scanf/test_scanfblocks.rb socket/test_basicsocket.rb socket/test_nonblock.rb socket/test_socket.rb stringio/test_stringio.rb strscan/test_stringscanner.rb
                 thread/test_queue.rb zlib/test_zlib.rb ruby/enc/test_big5.rb ruby/enc/test_cp949.rb ruby/enc/test_emoji.rb ruby/enc/test_euc_jp.rb ruby/enc/test_euc_kr.rb
                 ruby/enc/test_euc_tw.rb ruby/enc/test_gb18030.rb ruby/enc/test_gbk.rb ruby/enc/test_iso_8859.rb ruby/enc/test_koi8.rb ruby/enc/test_shift_jis.rb ruby/enc/test_utf16.rb
                 ruby/enc/test_utf32.rb ruby/enc/test_windows_1251.rb]
    end

    command = %w[test/mri/runner.rb -v --color=never --tty=no -q --]
    args.unshift(*command)
    raw_sh(env_vars, "#{JRUBY_DIR}/bin/jruby", *jruby_args, *args)
  end

  def test(*args)
    return test_pe(*args.drop(1)) if args.first == 'pe'

    return test_mri(*args.drop(1)) if args.first == 'mri'

    options = %w[--excl-tag fails]
    if args.first == 'fast'
      args.shift
      options += %w[--excl-tag slow]
    end
    mspec 'run', *options, *args
  end

  def test_pe(*args)
    run('--graal', *args, 'test/truffle/pe/pe.rb')
  end
  private :test_pe

  def tag(path, *args)
    return tag_all(*args) if path == 'all'
    mspec 'tag', '--add', 'fails', '--fail', path, *args
  end

  # Add tags to all given examples without running them. Useful to avoid file exclusions.
  def tag_all(*args)
    mspec('tag', *%w[--unguarded --all --dry-run --add fails], *args)
  end
  private :tag_all

  def untag(path, *args)
    puts
    puts "WARNING: untag is currently not very reliable - run `jt test #{[path,*args] * ' '}` after and manually annotate any new failures"
    puts
    mspec 'tag', '--del', 'fails', '--pass', path, *args
  end

  def bench(command, *args)
    bench_dir = Utilities.find_bench
    env_vars = {
      "JRUBY_9000_DEV_DIR" => JRUBY_DIR,
      "GRAAL_BIN" => Utilities.find_graal,
    }
    bench_args = ["-I#{bench_dir}/lib", "#{bench_dir}/bin/bench"]
    case command
    when 'log'
     env_vars = env_vars.merge({'JRUBY_OPTS' => '-J-G:+TraceTruffleCompilation -J-G:+DumpOnError -J-G:+TraceTruffleInlining -J-G:-TruffleBackgroundCompilation'})
     bench_args += ['reference', 'jruby-9000-dev-truffle-graal', '--show-commands', '--show-samples', '--data','#{bench_dir}/results/signalBench']
    when 'debug'
      env_vars = env_vars.merge({'JRUBY_OPTS' => '-J-G:+TraceTruffleCompilation -J-G:+DumpOnError -J-G:+TruffleCompilationExceptionsAreThrown'})
      bench_args += ['score', 'jruby-9000-dev-truffle-graal', '--show-commands', '--show-samples']
      raise 'specify a single benchmark for run - eg classic-fannkuch-redux' if args.size != 1
    when 'reference'
      bench_args += ['reference', 'jruby-9000-dev-truffle-graal', '--show-commands', '--show-samples', '--data','#{bench_dir}/results/signalBench']
      args << "5" if args.empty?
    when 'compare'
      bench_args += ['compare-reference', 'jruby-9000-dev-truffle-graal']
      args << "5" if args.empty?
    else
      raise ArgumentError, command
    end
    raw_sh env_vars, "ruby", *bench_args, *args
  end

  def findbugs(report=nil)
    case report
    when 'report'
      sh 'tool/truffle-findbugs.sh', '--report'
      sh 'open', 'truffle-findbugs-report.html'
    when nil
      sh 'tool/truffle-findbugs.sh'
    else
      raise ArgumentError, report
    end
  end

  def install(arg)
    case arg
    when /.*suite.*\.py$/
      mvn 'package'
      mvn '-Pcomplete'

      suite_file = arg
      suite_lines = File.readlines(suite_file)
      version = Utilities.jruby_version

      [
        ['maven/jruby-complete/target', "jruby-complete"],
        ['truffle/target', "jruby-truffle"]
      ].each do |dir, name|
        jar_name = "#{name}-#{version}.jar"
        source_jar_path = "#{dir}/#{jar_name}"
        shasum = Digest::SHA1.hexdigest File.read(source_jar_path)
        jar_shasum_name = "#{name}-#{version}-#{shasum}.jar"
        FileUtils.cp source_jar_path, "#{File.expand_path('../..', suite_file)}/lib/#{jar_shasum_name}"
        line_index = suite_lines.find_index { |line| line.start_with? "      \"path\" : \"lib/#{name}" }
        suite_lines[line_index] = "      \"path\" : \"lib/#{jar_shasum_name}\",\n"
        suite_lines[line_index + 1] = "      \#\"urls\" : [\"http://lafo.ssw.uni-linz.ac.at/truffle/ruby/#{jar_shasum_name}\"],\n"
        suite_lines[line_index + 2] = "      \"sha1\" : \"#{shasum}\"\n"
      end

      File.write(suite_file, suite_lines.join())
    else
      raise ArgumentError, kind
    end
  end
end

class JT
  include Commands

  def main(args)
    args = args.dup

    if args.empty? or %w[-h -help --help].include? args.first
      help
      exit
    end

    case args.first
    when "rebuild"
      send(args.shift)
    when "build"
      command = [args.shift]
      command << args.shift if args.first == "truffle"
      send(*command)
    end

    return if args.empty?

    commands = Commands.public_instance_methods(false).map(&:to_s)

    abort "no command matched #{args.first.inspect}" unless commands.include?(args.first)

    begin
      send(*args)
    rescue
      puts "Error during command: #{args*' '}"
      raise $!
    end
  end
end

JT.new.main(ARGV)
