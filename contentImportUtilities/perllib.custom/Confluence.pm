#!/usr/bin/perl -w
package Confluence;
use strict;
use vars qw(@ISA @EXPORT @EXPORT_OK %EXPORT_TAGS $VERSION);
use Exporter;

use JSON;
use MIME::Base64;
use REST::Client;
use LWP::UserAgent;
use HTTP::Request::Common;

use Data::Dumper;
use Date::Manip::Date;
use Confluence::Object;
use Confluence::Utilities;

$VERSION     = sprintf "%d", q$Revision: 1$ =~ /(\d+)/g;
@ISA         = ('Exporter', 'Confluence::Object');
@EXPORT      = ();
@EXPORT_OK   = ();
%EXPORT_TAGS = ();

# This module is subclassed from Confluence::Object.  Under the hood,
# it is a blessed hash.  When the object is instantiated through 'Confluence'->new(),
# it will be created and call the method "_Init()" at the bottom of the script.  Init
# will use the three variables below, @_accessors, @_bAccessors and @_aliases to create
# object methods.  Entries in those lists that are array references are used to set the
# default value.  The @_accessors store arbitrary values.  @_bAccessors is used for saving
# boolean values.  @_aliases creates multiple names to the same accessor.  _Init() will then
# pass control to the subroutine "load" for object customization.
#
# Example Usage:
#
# $confluence->new()
# $confluence->confluenceUrl( "http://localhost:1990/confluence" );   # set a value
# $theUrl = $confluence->confluenceUrl();                             # read a value.
#
# $confluence->bDebug( 1 ); or $confluence->bDebug( "true" );         # set a boolean
# if( $confluence->bDebug ){                                          # read the boolean
# }
#
our @_accessors  = ( @Confluence::Object::_accessors,
                     ['confluenceUrl', 'https://wiki2dev.collaboration.is.keysight.com'],
                     'restClient',
                     'lastResponse',
                     'dateFactory',
                     'author',
                     'creationDate',
                     'browser',
                   );
our @_bAccessors = ( @Confluence::Object::_bAccessors,
                     'bCredentialsDirty',
                     'bAuthorizationIncluded',
                     ['bPrintWarnings', 1],
                     ['bDebug', 0],
                   );
our @_aliases    = ( @Confluence::Object::_aliases,
                   );

# ---------------------------------------------------------------------------- #
# Object Methods
# ---------------------------------------------------------------------------- #
sub load
{
   my $self = shift;

   $self->restClient( 'REST::Client'->new() );
   $self->browser( 'LWP::UserAgent'->new() );
   $self->dateFactory( new Date::Manip::Date );

   return;
}
# ---------------------------------------------------------------------------- #
sub syncAllDirectories
{
   my $self      = shift;
   my %args = {};
   my %response;
   my $response;
   my %entity;
   my $responseContent;
   my $url;

   $url = $self->confluenceUrl."/rest/confluence-support/1.0/directory/sync",

   warn( "Arguments TO: $url\n" . Dumper( %args ) . "\n" ) if $self->bDebug;
   
   $self->addCredentials;
   $self->restClient->GET( $url.$self->restClient->buildQuery( %args ) );

   $responseContent = $self->restClient->responseContent();
   $self->lastResponse( $responseContent );

   warn( "Response FROM:\n" . Dumper( $responseContent ) . "\n" ) if $self->bDebug;

   eval{
      $response = decode_json($responseContent);
      use Data::Dumper;
      warn( Dumper( $response ) );
      %response = %{ $response } if ref( $response ) eq 'HASH';
      if( ref( $response{'results'} ) eq 'ARRAY' ){
         %entity = %{ $response{'results'}->[0] } if ref( $response{'results'}->[0] ) eq 'HASH';
      }
   };

   return ("ok");
}
# ---------------------------------------------------------------------------- #
sub getPageId
{
   my $self      = shift;
   my $spaceKey  = shift;
   my $pageTitle = shift;
   my %args;
   my %response;
   my $response;
   my %entity;
   my $responseContent;
   my $url;

   $url = $self->confluenceUrl . "/rest/api/content";

   $args{ 'type'     } = 'page';
   $args{ 'spaceKey' } = $spaceKey;
   $args{ 'title'    } = $pageTitle;

   warn( "Arguments TO: $url\n" . Dumper( %args ) . "\n" ) if $self->bDebug;
   
   $self->addCredentials;
   $self->restClient->GET( $url.$self->restClient->buildQuery( %args ) );

   $responseContent = $self->restClient->responseContent();
   $self->lastResponse( $responseContent );

   warn( "Response FROM:\n" . Dumper( $responseContent ) . "\n" ) if $self->bDebug;

   eval{
      $response = decode_json($responseContent);
      %response = %{ $response } if ref( $response ) eq 'HASH';
      if( ref( $response{'results'} ) eq 'ARRAY' ){
         %entity = %{ $response{'results'}->[0] } if ref( $response{'results'}->[0] ) eq 'HASH';
      }
   };

   return ($entity{'id'});
}
# ---------------------------------------------------------------------------- #
sub getPageContents
{
   my $self      = shift;
   my $pageId    = shift;
   my %args;
   my %response;
   my $response;
   my %entity;
   my $responseContent;
   my $url;
   my $result;

   die( "getPageContents requires a valid PageId" ) unless $pageId =~ /^\d+$/;

   $url = $self->confluenceUrl . "/rest/api/content/$pageId";

   $args{ 'expand'     } = 'body.view';

   warn( "Arguments TO: $url\n" . Dumper( %args ) . "\n" ) if $self->bDebug;
   
   $self->addCredentials;
   $self->restClient->GET( $url.$self->restClient->buildQuery( %args ) );

   $responseContent = $self->restClient->responseContent();
   $self->lastResponse( $responseContent );

   warn( "Response FROM:\n" . Dumper( $responseContent ) . "\n" ) if $self->bDebug;

   eval{
      $response = decode_json($responseContent);
      warn( "Decoded Response FROM:\n" . Dumper( $response ) . "\n" ) if $self->bDebug;
      %response = %{ $response } if ref( $response ) eq 'HASH';
      if( ref( $response{'body'} ) eq 'HASH' && ref( $response{'body'}{'view'} ) eq 'HASH' ){
         $result = $response{'body'}->{'view'}->{'value'};
      }
   };

   return ($result);
}
# ---------------------------------------------------------------------------- #
sub createPage
{
   my $self      = shift;
   my $spaceKey  = shift;
   my $pageTitle = shift;
   my $content   = shift;
   my %options   = %{ shift( @_ ) } if $#_ >= 0 and ref( $_[0] ) eq 'HASH';
   my %args;
   my %response;
   my $response;
   my $responseContent;
   my $id;
   my $newId;
   my $url;

   $url = $self->confluenceUrl . "/rest/api/content";

   $args{ 'type'     } = 'page';
   $args{ 'space'    } = {'key' => $spaceKey};
   $args{ 'title'    } = $pageTitle;
   $args{ 'body'     } = { 'storage' => { 'value' => $content,
                                          'representation' => 'storage' } };
 
   if( $options{ 'PARENT_PAGE_TITLE' } ){
      $id = $self->getPageId( $spaceKey, $options{ 'PARENT_PAGE_TITLE' } );
      $args{ 'ancestors' } = [{ 'type' => 'page', 'id' => $id }];
   } elsif( $options{ 'PARENT_PAGE_ID' } ){
      $args{ 'ancestors' } = [{ 'type' => 'page', 'id' => $options{'PARENT_PAGE_ID'} }];
   }

   warn( "Arguments TO: $url\n" . Dumper( %args ) . "\n" ) if $self->bDebug;

   $self->addCredentials;
   $self->restClient->POST($url,
                           encode_json( \%args ),
                           {"Content-Type"=>"application/json"});

   $responseContent = $self->restClient->responseContent();
   $self->lastResponse( $responseContent );

   warn( "Response FROM:\n" . Dumper( $responseContent ) . "\n" ) if $self->bDebug;

   if ($self->restClient->responseCode() < 300) {
      $response = decode_json($responseContent);
      %response = %{ $response } if ref( $response ) eq 'HASH';
      $newId = $response{'id'};

      if( $newId and $self->author ){
         $self->setCreatorAndCreationDate( $response{'id'}, $self->author, $self->creationDate );
      }
   }
   else {
      warn "Error posting to /rest/api/content with \n\n" 
           . Dumper( %args ) . "\n\n" 
           . "Response: " . Dumper( $responseContent ) . "\n" if $self->bPrintWarnings;
      $newId = undef;
   }

   return $newId;
}
# ---------------------------------------------------------------------------- #
sub updatePage
{
   my $self      = shift;
   my $spaceKey  = shift;
   my $pageTitle = shift;
   my $content   = shift;
   my %options   = %{ shift( @_ ) } if $#_ >= 0 and ref( $_[0] ) eq 'HASH';
   my $pageId;
   my %args;
   my %response;
   my $response;
   my $responseContent;
   my $id;
   my $newId;
   my $currentVersion;
   my $currentTitle;
   my $currentSpaceKey;
   my $url;

   warn( "Getting Page ID.\n" ) if $self->bDebug;
   $pageId = $self->getPageId( $spaceKey, $pageTitle );

   $url = $self->confluenceUrl . "/rest/api/content/$pageId";

   $args{ 'type'     } = 'page';
   $args{ 'id'       } = $pageId;
   $args{ 'body'     } = { 'storage' => { 'value' => $content,
                                          'representation' => 'storage' } };
 
   warn( "Getting Page Title and Version Number.\n" ) if $self->bDebug;
   warn( "Arguments TO: $url\n" . Dumper( %args ) . "\n" ) if $self->bDebug;

   $self->addCredentials;
   $self->restClient->GET( $url );

   $responseContent = $self->restClient->responseContent();
   $self->lastResponse( $responseContent );

   warn( "Response FROM:\n" . Dumper( $responseContent ) . "\n" ) if $self->bDebug;

   if ($self->restClient->responseCode() < 300) {
      $response = decode_json($responseContent);

      eval{ 
         $currentTitle    = $response->{'title'};
         $currentVersion  = $response->{'version'}->{'number'};
      };
   }

   if( $currentVersion =~ /^\d+$/ ){
      $args{ 'version' } = {'number' => $currentVersion + 1 };
      $args{ 'space'   } = {'key'    => $spaceKey };
      
      if( $options{ 'TITLE' } and $options{ 'TITLE' } =~ /\w/ ){
         $args{ 'title' } = $options{ 'TITLE' };
      } else {
         $args{ 'title' } = $currentTitle;
      }

      warn( "Updating the page.\n" ) if $self->bDebug;
      warn( "Arguments TO: $url\n" . Dumper( %args ) . "\n" ) if $self->bDebug;

      $self->restClient->PUT($url,
                              encode_json( \%args ),
                              {"Content-Type"=>"application/json"});

      $responseContent = $self->restClient->responseContent();
      $self->lastResponse( $responseContent );

      warn( "Response FROM:\n" . Dumper( $responseContent ) . "\n" ) if $self->bDebug;

      if ($self->restClient->responseCode() < 300) {
         $response = decode_json($responseContent);
         %response = %{ $response } if ref( $response ) eq 'HASH';
         $newId = $response{'id'};
      }
      else {
         warn "Error updating content with id: $pageId\n\n"
              . Dumper( %args ) . "\n\n" 
              . "Response: " . Dumper( $responseContent ) . "\n" if $self->bPrintWarnings;
         $newId = undef;
      }
   }

   return $newId;
}
# ---------------------------------------------------------------------------- #
sub addAttachment
{
   my $self       = shift;
   my $pageId     = shift;
   my $attachment = shift;
   my %args;
   my %debugArgs;
   my %response;
   my $response;
   my $responseContent;
   my $id;
   my $url;

   $url = $self->confluenceUrl . "/rest/api/content/$pageId/child/attachment";

   $args{ 'Content_Type' } = 'form-data';
   $args{ 'Content'      } = [ 'file' => [$attachment] ];
   $args{ 'X-Atlassian-Token' } = 'no-check';
   $args{ 'Authorization'     } = 'Basic ' . $self->credentials;

   %debugArgs = %args;
   $debugArgs{ 'Content' } = [ 'file' => "binary file content hidden" ];

   if( -f $attachment ){

      warn( "Arguments TO: $url\n" . Dumper( %debugArgs ) . "\n" ) if $self->bDebug;

      $responseContent = $self->browser()->request( POST( $url, %args ) );
      $self->lastResponse( $responseContent );

      warn( "Response FROM:\n" . Dumper( $responseContent ) . "\n" ) if $self->bDebug;

      #Check the outcome of the response
      if ($responseContent->is_success) {
         $response = decode_json( $responseContent->content );
      }
      else {
         die "Error attaching $attachment to $url\n\n" . $response->status_line . "\n";
      }

      eval{
         $id = $response->{'results'}->[0]->{'id'};
         $id =~ s/^att//g;
      };

      if( $id and $self->author ){
         $self->setCreatorAndCreationDate( $id, $self->author, $self->creationDate );
      }
   }

   return $id;
}
# ---------------------------------------------------------------------------- #
sub setCreatorAndCreationDate
{
   my $self         = shift;
   my $contentId    = shift;
   my $creator      = shift;
   my $creationDate = shift;
   my %args;
   my %response;
   my $response;
   my %entity;
   my $date;

   $args{ 'content-id' } = $contentId;
   $args{ 'creator'    } = $creator;
 
   if( $creationDate ){
      if( ref( $creationDate ) eq 'Date::Manip::Date' ){
         $date = $creationDate;
      } elsif( $creationDate ){
         $date = $self->dateFactory( new Date::Manip::Date );
         $date->parse( $creationDate );
      }

      $args{ 'creation-date' } = $date->printf( "%m/%d/%Y %H:%M:%S" );
   }


   warn( Dumper( %args ) . "\n" ) if $self->bDebug;

   $self->addCredentials;
   $self->restClient->POST($self->confluenceUrl."/rest/confluence-support/1.0/content",
                           encode_json( \%args ),
                           {"Content-Type"=>"application/json"});
   %response = %{ $response } if ref( $response ) eq 'HASH';

   # should put some error checking in here, but not sure exactly how to check for erros
   # quite yet.
   return;
}
# ---------------------------------------------------------------------------- #
sub addCredentials
{
   my $self = shift;
   if( not $self->bAuthorizationIncluded and $self->credentials ){
      $self->restClient->addHeader( 'Authorization', 'Basic ' . $self->credentials );
      $self->bAuthorizationIncluded( 1 );
   }
   return;
}
# ---------------------------------------------------------------------------- #
sub credentials
{
   my $self = shift;
   my $key  = 'credentials';
   my $return;

   if( $#_ >= 0 ){
      $self->accessor( $key, @_ );
      $self->bCredentialsDirty( 0 );
   }

   if( $self->accessor($key) and not $self->bCredentialsDirty ){
      $return = $self->accessor( $key );
   } elsif( $self->username and $self->password ){
      $return = $self->credentials( encode_base64( $self->username . ";" . $self->password ) );
   }

   return $return;
}
# ---------------------------------------------------------------------------- #
sub username
{
   my $self = shift;
   my $key  = 'username';
   if( $#_ >= 0 ){ $self->bCredentialsDirty(1); }
   return( $self->accessor( $key, @_ ) );
}
# ---------------------------------------------------------------------------- #
sub password
{
   my $self = shift;
   my $key  = 'password';
   if( $#_ >= 0 ){ $self->bCredentialsDirty(1); }
   return( $self->accessor( $key, @_ ) );
}

# ---------------------------------------------------------------------------- #
sub _Init
{
   use strict;
   my $self = shift;
   my %args = ();

   $self->addVariables(        @_accessors  ) if $#_accessors  >= 0;
   $self->addBooleanVariables( @_bAccessors ) if $#_bAccessors >= 0;
   $self->createAliases(       @_aliases    ) if $#_aliases    >= 0;
   
   # default variables...
   $self->load( @_ );

   return( $self );
}
1;
