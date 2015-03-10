# Copyright (c) 2014 Oracle and/or its affiliates. All rights reserved. This
# code is released under a tri EPL/GPL/LGPL license. You can use it,
# redistribute it and/or modify it under the terms of the:
# 
# Eclipse Public License version 1.0
# GNU General Public License version 2
# GNU Lesser General Public License version 2.1

module Bench
  module Commands

    class Report < Command
      
      REPORT_DIR = "#{File.dirname(__FILE__)}/../report"

      def after(options, measurements)
        report = read("report.html")
        
        replacements = {
          '<script src="jquery.js"></script>' =>
            "<script>#{read('jquery.js')}</script>",
            
          '<script src="bootstrap.js"></script>' =>
            "<script>#{read('bootstrap.js')}</script>",

          '<script src="chart.js"></script>' =>
            "<script>#{read('chart.js')}</script>",
          
          '<script src="data.js"></script>' =>
            "<script>bench_data = #{JSONFormatter.format(options, measurements)};</script>",
          
          '<script src="report.js"></script>' =>
            "<script>#{read('report.js')}</script>",
          
          '<link rel="stylesheet" type="text/css" href="bootstrap.css">' =>
            "<style>#{read('bootstrap.css')}</style>",
          
          '<link rel="stylesheet" type="text/css" href="bootstrap-theme.css">' =>
            "<style>#{read('bootstrap-theme.css')}</style>"
        }

        replacements.each do |find, replace|
          report[find] = replace
        end

        if options.flags.has_key? "--baseline"
          report["var speedup_reference_implementation = bench_data.implementations[0];"] =
            "var speedup_reference_implementation = \"#{options.flags['--baseline']}\";"
        end

        report["<!-- notes -->"] = "<p>Report generated at #{Time.now}</p><!-- notes -->"

        if options.flags.has_key? "--notes"
          report["<!-- notes -->"] = File.open(options.flags["--notes"]).read
        end

        File.open("report.html", "w").write(report)

        puts "Total benchmarking time: #{(measurements.total_time/60).round}m"
      end

      def read(file)
        File.open("#{REPORT_DIR}/#{file}", "r").read()
      end

    end

  end
end
