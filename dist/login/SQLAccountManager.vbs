'Get Java path.
Dim path
Set shell = WScript.CreateObject("WScript.Shell")
path = shell.Environment.Item("JAVA_HOME")
If path = "" Then
	MsgBox "Could not find JAVA_HOME environment variable!", vbOKOnly, "SQL Account Manager"
Else
	If InStr(path, "\bin") = 0 Then
		path = path + "\bin\"
	Else
		path = path + "\"
	End If
	path = Replace(path, "\\", "\")
	path = Replace(path, "Program Files", "Progra~1")
End If

'Run the installer.
shell.Run "cmd /c start ""L2JProjectX - SQL Account Manager"" " & path & "java -Djava.util.logging.config.file=console.cfg -cp ./../libs/* org.l2jpx.tools.accountmanager.SQLAccountManager", 1, False
