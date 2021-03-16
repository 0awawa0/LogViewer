<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>LogViewer</title>
</head>
<body style="margin: 0; padding: 0; background-color:#383838;">
    <div style="position: sticky; top: 0; padding-left: 25px; padding-bottom: 10px; background-color:#383838;">
        <a href="/"><h1 style="color:#ffffff;">LogViewer</h1></a>
        <form enctype="multipart/form-data" method="post">
            <p><input type="file" name="logFile" accept="text/plain" style="color: #ffffff;">
            <input type="submit" value="Отправить"></p>
        </form>
    </div>

    <div style="background: #212121; left: 0; right: 0; padding: 15px">
        <#list entries as entry>
            <p><font color="#525252">${entry.row}: </font><font color="${entry.color}">${entry.text}</font></p>
        </#list>
    </div>
</body>
</html>