#!/usr/bin/perl -w
package Confluence::Utilities;
use strict;
use vars qw(@ISA @EXPORT @EXPORT_OK %EXPORT_TAGS $VERSION);
use Exporter;
$VERSION     = sprintf "%d.%02d", q$Revision: 1.22 $ =~ /(\d+)/g;
@ISA         = ('Exporter');
@EXPORT      = ("&aUnion",
                "&bItemIn",
                "&sReadFile",
                "&sReadBinaryFile",
                "&stripSpaces"
                );
@EXPORT_OK   = ();
%EXPORT_TAGS = ();

# ---------------------------------------------------------------------- #
sub aUnion
{
   use strict;
   my @array = @_;
   my %hash = ();

   foreach $_ (@array)
   {
      $hash{ $_ }++ if $_ =~ /\w/;
   }

   return( sort keys( %hash ) );
}
# ---------------------------------------------------------------------- #
sub bItemIn
{
   use strict;
   my $item      = shift;
   my $itemRegEx = $item;
   my @array     = @_;
   my $bFlag     = undef;
   my @matches;
   my $regularExpression;
   my @regularExpressions;
   my $i;

   # escape regular expression characters...
   $itemRegEx =~ s/\\/\\\\/g;
   $itemRegEx =~ s/\//\\\//g;
   $itemRegEx =~ s/\(/\\\(/g;
   $itemRegEx =~ s/\)/\\\)/g;
   $itemRegEx =~ s/\{/\\\{/g;
   $itemRegEx =~ s/\}/\\\}/g;
   $itemRegEx =~ s/\[/\\\[/g;
   $itemRegEx =~ s/\]/\\\]/g;
   $itemRegEx =~ s/\./\\\./g;
   $itemRegEx =~ s/\+/\\\+/g;
   $itemRegEx =~ s/\*/\\\*/g;
   $itemRegEx =~ s/\?/\\\?/g;
   $itemRegEx =~ s/\^/\\\^/g;
   $itemRegEx =~ s/\$/\\\$/g;

   @matches = grep { /^$itemRegEx$/ } (@array);

   if( $#matches >= 0 )
   {
      $bFlag = 1;
   }
   else
   {
      @regularExpressions = grep { ref( $_ ) eq 'Regexp' } (@array);
      foreach $regularExpression (@regularExpressions)
      {
         if( $item =~ /$regularExpression/ )
         {
            $bFlag = 1;
            last;
         }
      }
   }
   
   return( $bFlag );
}
# ---------------------------------------------------------------------- #
sub sReadFile
{
  use strict;
  local $/; # Effectively undefine INPUT_RECORD_SEPARATOR, this enables slurping
  
  my $fileName = shift;
  my $fileContents;

  if( open( FILE, "<$fileName") )
  {
     # Slurp in the entire file
     $fileContents = <FILE>; 

     close( FILE ) or die( "Failed to close $fileName\n" );
  }

  return $fileContents;
}
# ---------------------------------------------------------------------- #
sub sReadBinaryFile
{
  use strict;
  local $/; # Effectively undefine INPUT_RECORD_SEPARATOR, this enables slurping
  
  my $fileName = shift;
  my $fileContents;

  if( open( FILE, "<$fileName") )
  {
     binmode( FILE );

     # Slurp in the entire file
     $fileContents = <FILE>; 
  
     close( FILE ) or die( "Failed to close $fileName\n" );
  }

  return $fileContents;
}
# ---------------------------------------------------------------------- #
sub stripSpaces
{
   use strict;
   my @buffer = @_;
   for( @buffer )
   {
      s/^\s+//;
      s/\s+$//;
   }
   return wantarray ? @buffer : $buffer[0]
}
1;
