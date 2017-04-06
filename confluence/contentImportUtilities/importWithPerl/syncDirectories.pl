#!/usr/bin/perl
use strict;
use FindBin;
use lib "$FindBin::Bin/../perllib.custom";
use Getopt::Std;
use File::Basename;
use MIME::Base64;

use Confluence;
use Confluence::Utilities;
$| = 1;

my %options;

my $usage = "This is an example script for using Perl to sync the remote directories on an \n"
          . "operational instance of Confluence. This script requires the plugin confluence-support\n"
          . "to be installed and active.\n"
          . "\n"
          . "https://wiki2.collaboration.is.keysight.com/display/confluencePluginDevelopment\n"
          . "\n"
          . "By default, the script will look to find the user credentials in a file called \"credentials.txt\"\n"
          . "located in the current directory.  The file can be created using the script \"makeCredentials.pl\"\n"
          . "The credentials can also be passed in directly using the switches \"-U username -P password\" or\n"
          . "explicitly pointing to a credentials file with \"-C credentialsFile.txt\"\n"
          . "\n"
          . "-U [username]\n"
          . "-P [password]\n"
          . "-C [credentialsFile]\n"
          . "\n"
          . "-u [confluence url]   => such as http://localhost:1990/confluence\n"
          . "-d                      => debug mode (same as verbose mode)\n"
          . "-v                      => verbose mode\n";

my $confluence      = 'Confluence'->new();
my $credentialsFile = 'credentials.txt';
my $confluenceUrl   = 'http://localhost:1990/confluence';
my $spaceKey        = 'ds';
my $pageTitle       = 'Welcome to the Confluence Demonstration Space';
my $pageId;
my $pageContents;
my $action;
my $bDebug;
my $newPageTitle;
my $content;
my %updateOptions;

getopts( "U:P:C:u:dv", \%options );

# -------------------------------------------------------------------------------------- #
# Override defaults with command line pass parameters
# -------------------------------------------------------------------------------------- #
$confluenceUrl = $options{'u'} if $options{'u'};
$bDebug        = 1             if $options{'d'};
$bDebug        = 1             if $options{'v'};

# -------------------------------------------------------------------------------------- #
# Initialize the Confluence Object.  Under the hood, this is just a
# hash with associated methods to keep track of things like the user
# credentials and confluence URL.
# -------------------------------------------------------------------------------------- #
$confluence->confluenceUrl( $confluenceUrl );
if( $options{'U'} and $options{'P'} ){
   $confluence->credentials( encode_base64( $options{'U'} . ":" . $options{'P'} ));
} else {
   $credentialsFile = $options{'C'} if $options{'C'} and -e $options{'C'};
   $confluence->credentials( sReadFile( $credentialsFile ) );
}

$confluence->bDebug( 1 ) if $bDebug;

print( $confluence->syncAllDirectories() . "\n" );
