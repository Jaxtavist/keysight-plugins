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

my $usage = "This is an example script for using Perl to interact with the Confluence REST API.\n"
          . "Two of the methods - set page author and set page creation date - require the Entity \n"
          . "Authorship plugin as the default Confluence REST Interface does not support those operations.\n"
          . "\n"
          . "https://bitbucket.org/selberg/entity-authorship\n"
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
          . "-s [spaceKey]\n"
          . "-p [pageTitle] sets the page to get, attach a file to or the parent page for page creation\n"
          . "\n"
          . "-g                      => get the page\n"
          . "-c [filename]           => create a new page with the title\n"
          . "-n [filename]           => update an existing page with New content\n"
          . "-a [attachment]         => attach the specified file to the page\n"
          . "-t [title for new page] => Set the title for a new or updated page.  Defaults to the filename or the existing page name.\n"
          . "-d                      => debug mode (same as verbose mode)\n"
          . "-v                      => verbose mode\n"
          . "\n"
          . "Examples (expecting to upload to the demo space of an SDK instance of Confluence on the localhost)\n"
          . "\n"
          . "Get Page ID\n"
          . "$0 -g\n"
          . "\n"
          . "Upload HTML to a New Page\n"
          . "$0 -c new.html -t \"New Page\"\n"
          . "\n"
          . "Update a Page with new content\n"
          . "$0 -n update.html -t \"Updated Page\"\n"
          . "\n"
          . "Upload Attachment to an existing page\n"
          . "$0 -a BlueCircle.png -p \"Updated Page\"\n";

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

getopts( "U:P:C:u:s:p:gc:n:a:t:dv", \%options );

if( $options{'g'} ){
   $action = "GET_PAGE";
} elsif( $options{'c'} and -f $options{ 'c' } ){
   $action = "CREATE_PAGE";
} elsif( $options{'n'} and -f $options{ 'n' } ){
   $action = "UPDATE_PAGE";
} elsif( $options{'a'} and -f $options{ 'a' } ){
   $action = "ATTACH_TO_PAGE";
} else { 
   die $usage; 
}


# -------------------------------------------------------------------------------------- #
# Override defaults with command line pass parameters
# -------------------------------------------------------------------------------------- #
$confluenceUrl = $options{'u'} if $options{'u'};
$spaceKey      = $options{'s'} if $options{'s'};
$pageTitle     = $options{'p'} if $options{'p'};

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

# -------------------------------------------------------------------------------------- #
# Get a Page's ID
# -------------------------------------------------------------------------------------- #

if( $action =~ /GET_PAGE/ ){
   print "Getting Page\n\n";
   $pageId = $confluence->getPageId( $spaceKey, $pageTitle );
   if( $pageId and $pageId =~ /\w/ ){
      print( "Page Found: $pageId\n" );
    
      $pageContents = $confluence->getPageContents( $pageId );
      if( $pageContents and $pageContents =~ /\w/ ){
         print( "Page Contents;\n$pageContents\n" );
      } else {
         print( "No Page Contents Found\n" );
      }

   } else {
      print( "Page Not Found\n" );
   }

} elsif( $action =~ /CREATE_PAGE/ ){
   print "Creating Page\n\n";

   # Uncomment the below to use the Entity Authorship Rest methods to set the author and creation date
   # $confluence->author( $author );      # Needs to be a known userid in Confluence
   # $confluence->creationDate( $date );  # Date::Manip object or parseable string

   if( $options{ 't' } ){
      $newPageTitle = $options{'t'};
   } else {
      $newPageTitle = $options{'c'};
   }
   $content = sReadFile( $options{ 'c' } );
  
   $pageId = $confluence->createPage( $spaceKey, $newPageTitle, $content, { 'PARENT_PAGE_TITLE' => $pageTitle } );
   if( $pageId and $pageId =~ /\w/ ){
      print( "Created Page: $pageId\n" );
   }
} elsif( $action =~ /UPDATE_PAGE/ ){
   print "Updating Page\n\n";

   # Uncomment the below to use the Entity Authorship Rest methods to set the author and creation date
   # $confluence->author( $author );      # Needs to be a known userid in Confluence
   # $confluence->creationDate( $date );  # Date::Manip object or parseable string

   if( $options{ 't' } ){
      $updateOptions{ 'TITLE' } = $options{'t'};
   }
   $content = sReadFile( $options{ 'n' } );
  
   $pageId = $confluence->updatePage( $spaceKey, $pageTitle, $content, \%updateOptions );
   if( $pageId and $pageId =~ /\w/ ){
      print( "Created Page: $pageId\n" );
   }
} elsif( $action =~ /ATTACH_TO_PAGE/ ){

   # Uncomment the below to use the Entity Authorship Rest methods to set the author and creation date
   # $confluence->author( $author );      # Needs to be a known userid in Confluence
   # $confluence->creationDate( $date );  # Date::Manip object or parseable string

   print "Getting Page Id\n\n";
   $pageId = $confluence->getPageId( $spaceKey, $pageTitle );
   print "Attaching To Page\n\n";
   $pageId = $confluence->addAttachment( $pageId, $options{ 'a' } );

   if( $pageId and $pageId =~ /\w/ ){
      print( "Attachment Added: $pageId\n" );
   }
}




