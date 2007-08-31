Dir.chdir ARGV[0] unless ARGV.empty?
File.open('/tmp/all-otrunk.xml','w') do |f| 
  f.write "<otrunk>\n"
  f.write `find . -name "*.otml" -print0 | xargs -0  egrep -h -o "<import class=.*" | sed 's/ *\\/>.*/\\/>/' | sort | uniq`
  f.write "</otrunk>\n"
end