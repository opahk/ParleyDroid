require 'rubygems'
require 'rake'
require 'rexml/document'
require 'rake/clean'

def manifest
  @parsed ||= begin
    doc = REXML::Document.new(File.read('AndroidManifest.xml'))
    {
      :package => doc.root.attribute('package').to_s
    }
  end
end

app_pkg = manifest[:package]
project = app_pkg.gsub(/\./, '_')

sdk_location = ENV['ANDROID_SDK'] || '/Users/jan/projects/android-sdk-mac_x86-1.5_r1'
src = 'src'
gen = 'gen'
res = 'res'
bin = 'bin'
libs = 'libs'
assets = 'assets'
classes = "#{bin}/classes"
ap_ = "#{bin}/#{project}.ap_"
apk = "#{bin}/#{project}.apk"

android_jar = "#{sdk_location}/platforms/android-1.5/android.jar"
android_aidl = "#{sdk_location}/platforms/android-1.5/framework.aidl"
intermediate_dex_location = "#{bin}/classes.dex"

directory gen
directory bin
directory classes
dirs = [gen, bin, classes]

CLEAN.include(gen, bin)
CLASSPATH = FileList["#{libs}/**/*.jar"]
BOOTCLASSPATH = FileList[android_jar]

# Extensions for standard rake classes.
module Rake
  class FileList
    def to_cp(sep = File::PATH_SEPARATOR)
      self.join(sep)
    end
  end
end

def compile(dest, *srcdirs)
  files = FileList.new
  srcdirs.each do |d|
    files.include("#{d}/**/*.java")
  end

  sh("javac", "-target", "1.5", "-g", "-bootclasspath", BOOTCLASSPATH.to_cp,  "-nowarn", "-Xlint:none", "-sourcepath", srcdirs.join(File::PATH_SEPARATOR), "-d", dest ,"-classpath", CLASSPATH.to_cp, *files)
end

task :default => :debug

task :resource_src => dirs do
  sh "aapt package -m -J #{gen} -M AndroidManifest.xml -S #{res} -I #{android_jar}"
end


task :aidl => dirs do
  FileList["#{src}/**/*.aidl"].each do |f|
    sh "aidl -p #{android_aidl} -I #{src} -o #{gen} #{f}"
 end
end

task :compile => [:resource_src, :aidl] do
  compile(classes, src, gen)
end

task :dex => :compile do
  sh "dx", *["--dex", "--output=#{intermediate_dex_location}", classes ] + CLASSPATH
end

task :package_resources do
  opts = ["package", "-f", "-M", "AndroidManifest.xml", "-I", android_jar, "-S", res, "-F", ap_]
  opts += ["-A", assets] if File.directory?(assets)
  sh "aapt", *opts
end


desc "Builds the application and sign it with a debug key."
task :debug => [:dex, :package_resources] do
  args = [apk, "-f", intermediate_dex_location, "-rf", src, "-z", ap_]
  args += [ "-rj", libs] if File.directory?(libs)

  sh "apkbuilder", *args
end


def adb(*args)
    args.unshift '-s', ENV['DEVICE'] if ENV['DEVICE']
    sh "adb", *args 
end

desc "Installs the debug package onto a running emulator or device. This can only be used if the application has not yet been installed."
task :install => :debug do
  adb 'install', apk
end

desc "Installs the debug package on a running emulator or device that already has the application."
task :reinstall => :debug do
  adb 'install', '-r', apk
end

desc "uninstall the application from a running emulator or device."
task :uninstall do
  adb 'uninstall',  app_pkg
end



