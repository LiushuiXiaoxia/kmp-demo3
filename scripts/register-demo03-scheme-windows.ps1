param(
    [Parameter(Mandatory = $true)]
    [string]$AppExecutable
)

$schemeKey = "HKCU:\Software\Classes\demo03"
$commandKey = "$schemeKey\shell\open\command"

New-Item -Path $schemeKey -Force | Out-Null
Set-ItemProperty -Path $schemeKey -Name "(Default)" -Value "URL:Demo03 Protocol"
Set-ItemProperty -Path $schemeKey -Name "URL Protocol" -Value ""

New-Item -Path $commandKey -Force | Out-Null
Set-ItemProperty -Path $commandKey -Name "(Default)" -Value "`"$AppExecutable`" `"%1`""

Write-Host "Registered demo03:// handler for $AppExecutable"
