
######### AutoJotter ######## 
~ ~ Hand writing software ~ ~
----------[v1.0]-------------

AutoJotter is open source handwriting software for pen plotters that generates movement instructions from plain text.

* Provide AutoJotter with plain text to generate universal G-Code programs for home pen plotters and other machines.
* Writes realistic hand printing in a single stroke, does not outline fonts like most tools.
* Can write characters of any size.
* GUI and command line for scripting.
* Customizable to different machines.
* Windows, Linux, Mac (Java 8 Required).
* Tabs, linebreaks, word wrap supported, writes most ASCII characters ! @ # $ % ^ & * ~ ? .
* Customize fonts by editing SVG file.

Double click autojotter.jar to run GUI.

Run "java -jar autojotter" with the following arguments to run command line.  Plain text
is sent to standard input and g-code is sent through standard output.  Text preview
is sent through standard error.
arg1: total width 
arg2: total height 
arg3: num cols
arg4: num rows
arg5: upcommand
arg6 = downcommand
arg7=feedrate
arg8=rotated(y/n)
arg9=italics(y/n)

eg 
echo hello | java -jar autojotter.jar 6 6.9 40 25 "S3 M500" "S3 M850" 50 y y


