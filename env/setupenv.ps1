"********************************************************************************"
"*                                                                              *"
"*                        O7 Development Console                            *"
"*                                                                              *"
"********************************************************************************"

# ==============================================================================
# Define local functions
# ==============================================================================

function warn { write-host -ForegroundColor yellow "WARN : $args" }

function error { write-host -ForegroundColor red "ERROR : $args"; "O7 build environment setup failed."; exit }

function catch_batch_env( $batch, $arg )
{
    $tag = "[[[===start-environment-table===]]]" # cannot have space here.
    $cmd = "`"$batch`" $arg &echo $tag& set"
    $tag_found = $false
    cmd.exe /c $cmd|foreach-object{
        if( $tag_found )
        {
            $p, $v = $_.split('=')
            Set-Item -path env:$p -value $v
        }
        elseif( $_ -eq $tag )
        {
            $tag_found = $true
        }
        else
        {
            $_
        }
    }
}

# ==============================================================================
# Define global functions
# ==============================================================================

# Define your function like this: function global:<name> (...) { .... }
function global:ccc { cmd.exe /c $args }

# redefine prompt function
function global:prompt()
{
	write-host -ForegroundColor Magenta "=== O7 Build Environment ===="
	write-host -ForegroundColor Magenta "[$(get-location)]"
	return ">"
}

# ==============================================================================
# Get the root directory
# ==============================================================================

# note: $O7_ROOT is a global variable that could be used in other places outside of this script.
$global:O7_ROOT=split-path -parent $MyInvocation.InvocationName|split-path -parent
$env:O7_ROOT=$O7_ROOT

# ==============================================================================
# setup aliases
# ==============================================================================

if( Test-Path -path "$O7_ROOT\env\alias.txt" )
{
    # create script block for all aliases
    $aliases = ""
    get-content "$O7_ROOT\env\alias.txt"|foreach {
        $name, $value = $_.split(' ')

        $body = ([System.String]$value).Trim( ' "' ).Replace( "cd /d", "cd" ).Replace( '$*', '$args' )
        $body = $body.Replace( "%O7_ROOT%", '$O7_ROOT' ).Replace( "%", "" )

        $aliases = $aliases +
        "
        function global:$name {$body}
        "
    }
    $aliases = $executioncontext.InvokeCommand.NewScriptBlock( $aliases )

    # run the script
    &$aliases
}
else
{
	warn "$O7_ROOT\env\alias.txt is missing."
}

# ==============================================================================
# MISC
# ==============================================================================

# detect host CPU type
$current_cpu="x86"
if( ("amd64" -ieq $env:PROCESSOR_ARCHITECTURE) -or ("AMD64" -ieq $env:PROCESSOR_ARCHITEWOW64) )
{
    $current_cpu="x64"
}
elseif( "ia64" -ieq $env:PROCESSOR_ARCHITECTURE )
{
    $current_cpu="ia64"
}

# Setup PATH
$MY_BIN_PATH = "$O7_ROOT\env\bin\mswin\x86"
if( "x64" -eq $current_cpu )
{
    $MY_BIN_PATH = "$O7_ROOT\env\bin\mswin\x64;$MY_BIN_PATH"
}
$env:Path = "$O7_ROOT\env\bin\mswin\cmd;$MY_BIN_PATH;$env:Path"

# update title
$Host.UI.RawUI.WindowTitle = "o7 Build Environment"

# change current location
cd $O7_ROOT

# ==============================================================================
# call user specific setup script
# ==============================================================================

if( Test-Path $O7_ROOT\user\$env:USERNAME.ps1 )
{
    .$O7_ROOT\user\$env:USERNAME.ps1
}

# ==============================================================================
# DONE
# ==============================================================================

write-host -ForegroundColor green "All done!"