require 'rubygems/test_case'
require 'rubygems/dependency_resolver'
require 'rubygems/available_set'

class TestGemDependencyResolverIndexSpecification < Gem::TestCase

  def test_initialize
    set     = Gem::DependencyResolver::IndexSet.new
    source  = Gem::Source.new @gem_repo
    version = Gem::Version.new '3.0.3'

    spec = Gem::DependencyResolver::IndexSpecification.new(
      set, 'rails', version, source, Gem::Platform::RUBY)

    assert_equal 'rails',             spec.name
    assert_equal version,             spec.version
    assert_equal Gem::Platform::RUBY, spec.platform

    assert_equal source, spec.source
  end

  def test_initialize_platform
    set     = Gem::DependencyResolver::IndexSet.new
    source  = Gem::Source::Local.new
    version = Gem::Version.new '3.0.3'

    spec = Gem::DependencyResolver::IndexSpecification.new(
      set, 'rails', version, source, Gem::Platform.local)

    assert_equal Gem::Platform.local.to_s, spec.platform
  end

  def test_spec
    specs = spec_fetcher do |fetcher|
      fetcher.spec 'a', 2
      fetcher.spec 'a', 2 do |s| s.platform = Gem::Platform.local end
    end

    source = Gem::Source.new @gem_repo
    version = v 2

    set = Gem::DependencyResolver::IndexSet.new
    i_spec = Gem::DependencyResolver::IndexSpecification.new \
      set, 'a', version, source, Gem::Platform.local

    spec = i_spec.spec

    assert_equal specs["a-2-#{Gem::Platform.local}"].full_name, spec.full_name
  end

  def test_spec_local
    a_2_p = util_spec 'a', 2 do |s| s.platform = Gem::Platform.local end
    Gem::Package.build a_2_p

    source = Gem::Source::Local.new
    set = Gem::DependencyResolver::InstallerSet.new :local
    set.always_install << a_2_p

    i_spec = Gem::DependencyResolver::IndexSpecification.new \
      set, 'a', v(2), source, Gem::Platform.local

    spec = i_spec.spec

    assert_equal a_2_p.full_name, spec.full_name
  end

end

