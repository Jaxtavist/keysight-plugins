#!/usr/bin/perl -w
########################################
#
# Obect - Base Class to build objects from
#
# Documentation written using POD
# (Plain Old Documentation)  see the
# last part of the file.
# 
# to read the documentation, try the manpage, local 
# perl documentation, or pod2html Object.pm > Object.html
#
# $Author: selberg $
# $Date: 2006-11-30 10:34:46-08 $
# $Revision: 1.3 $
#
# Copyright 4/24/2006, Keysight Technologies.
#
# This program is free software.  You may copy,
# modify, or redistribute it under the same terms
# as Perl itself.
########################################
package Confluence::Object;
use strict;
use vars qw(@ISA @EXPORT @EXPORT_OK %EXPORT_TAGS $VERSION);
use Exporter;
our $AUTOLOAD;
$VERSION = sprintf "%d.%02d", q$Revision: 1.3 $ =~ /(\d+)/g;
@ISA = ('Exporter');
@EXPORT      = ();
@EXPORT_OK   = ();
%EXPORT_TAGS = ();

our @_accessors  = ();
our @_bAccessors = ();
our @_aliases    = ();

sub _accessors{  return( @_accessors  ); }
sub _bAccessors{ return( @_bAccessors ); }
sub _aliases{    return( @_aliases    ); }
# ---------------------------------------------------------------------- #
sub new
{
   use strict;
   my $buffer = shift;
   my %hash;
   my $self;
   my $class;

   if( ref($buffer) )
   {
      $self = $buffer;
   }
   else
   {
      $class = $buffer;
      $self = {};
      
      bless( $self, $class );
   }

   $self->_Init( @_ );

   return($self);
}
# ---------------------------------------------------------------------- #
# To correctly build aliases and accessors, these rountines need to 
# be in the object that inherits from object.
sub _Init
{
   use strict;
   my $self = shift;

   $self->addVariables(        @_accessors  ) if $#_accessors  >= 0;
   $self->addBooleanVariables( @_bAccessors ) if $#_bAccessors >= 0;
   $self->createAliases(       @_aliases    ) if $#_aliases    >= 0;

   # default variables...
   $self->load( @_ );

   return( $self );
}
# ---------------------------------------------------------------------- #
sub load
{
   my $self = shift;
   return;
}
# ---------------------------------------------------------------------- #
sub bEnableAutoLoadingOfObjectMethods
{
   use strict;
   my $self = shift;
   my $key = '_bEnableAutoLoadingOfObjectMethods';

   if( defined( $_[0] ) )
   {
      if( $_[0] =~ /(1|true)/ )
      {
         $self->accessor( $key, 1 );
      }
      else
      {
         $self->accessor( $key, 0 );
      }
   }
   return( $self->accessor( $key ) );
}
# ---------------------------------------------------------------------- #
sub bEnableWarningForAutoLoadingOfObjectMethods
{
   use strict;
   my $self = shift;
   my $key = '_bEnableWarningForAutoLoadingOfObjectMethods';

   if( defined( $_[0] ) )
   {
      if( $_[0] =~ /(1|true)/ )
      {
         $self->accessor( $key, 1 )
      }
      else
      {
         $self->accessor( $key, 0 )
      }
   }
   return( $self->accessor( $key ) );
}
# ---------------------------------------------------------------------- #
sub AUTOLOAD
{
   use strict;
   my $self = shift;
   my @caller = caller();
   my $type = ref( $self ) or die( "Called by a non-object in $caller[0] at line $caller[2].\n" );
   my $key;
   my $method;
   my $package;
   
   #local $AUTOLOAD;
   return if ($AUTOLOAD =~ /::DESTROY/);

   ($package, $method) = ($1, $2) if $AUTOLOAD =~ /(.*)::(.*)/;
   $key = $method;

   if( $self->bEnableAutoLoadingOfObjectMethods and defined( $method ) and $method ne 'DESTROY' )
   {
      warn( "Autoloading Method -> \'$method\'\n" ) if $self->bEnableWarningForAutoLoadingOfObjectMethods;
      $self->accessor( $key, @_ );
      return $self->accessor( $key );
   }
   elsif( defined( $method ) and $method eq 'DESTROY' )
   {
   }
   else
   {
      @caller = caller(0);
      die "Can't locate object method \"$method\" via package \"$package\" at $caller[1] line $caller[2]\n";
      return;
   }
}
# ---------------------------------------------------------------------- #
sub param
{
   my $self = shift;
   return $self->accessor( @_ ) ;
}
# ---------------------------------------------------------------------- #
sub accessor
{
   my $self   = shift;
   my %buffer;
   my $key;
   my @return;
   my $return;

   # if a single scaler value, return the known values
   if( $#_ == 0 and not ref( $_[0] ) eq 'HASH' )
   {
      if( defined( $self->{ $_[0] } ) )
      {
         if( ref( $self->{ $_[0] } ) eq 'ARRAY' )
         {
            @return = @{ $self->{ $_[0] } };
            $return = @return == 1 ? $return[0] : [ @return ];
         }
         elsif( ref( $self->{ $_[0] } ) eq 'HASH' )
         {
            @return = %{ $self->{ $_[0] } };
            $return = { @return };
         }
         else
         {
            @return = ( $self->{ $_[0] } );
            $return = $return[0];
         }
      }
   }
   #single hash reference
   elsif( $#_ == 0 and ref( $_[0] ) eq 'HASH' )
   {
      %buffer = %{ $_[0] };
      if( defined( $buffer{ '-name' } ) )
      {
         if( defined( $buffer{ '-delete' } ) )
         {
            if( defined( $self->{ $buffer{ '-name' } } ) )
            {
               delete( $self->{ $buffer{ '-name' } } )
            }
         }
         elsif( defined( $buffer{ '-value' } ) )
         {
            $self->{ $buffer{ '-name' } } = $buffer{ '-value' };
            @return = $self->accessor( $buffer{ '-name' } );
            $return = $self->accessor( $buffer{ '-name' } );
         }
         else
         {
            @return = $self->accessor( $buffer{ '-name' } );
            $return = $self->accessor( $buffer{ '-name' } );
         }
      }
   }
   elsif( $#_ >= 1 )
   {
      $key = shift;
      $self->{ $key } = ( ( $#_ == 0) and ref( $_[0] ) ) ? $_[0] : [ @_ ];
      #$self->createAccessorsWithIndex( 2, $key );
      @return = $self->accessor( $key );
      $return = $self->accessor( $key );
   }
   else
   {
      @return = keys %{ $self };
      $return = [ @return ];
   }

   return wantarray ? @return : $return   
}
# ---------------------------------------------------------------------- #
sub createAccessors
{
   my $self = shift;
   return $self->createAccessorsWithIndex( 2, @_ );
}
# ---------------------------------------------------------------------- #
sub createAccessorsWithIndex
{
   use strict;
   no strict 'refs';
   my $self = shift;
   my $callerIndex = shift;
   my @potentialAccessors = @_;
   my @info = caller($callerIndex);
   my $package = $info[0];
   my @buffer;
   my $defaultValue;
   my $method;

   foreach my $potentialAccessor (@potentialAccessors)
   {

         if( ref( $potentialAccessor ) eq 'ARRAY' )
         {
            @buffer = @{ $potentialAccessor };
            $potentialAccessor = $buffer[0];
            $defaultValue      = $buffer[1];
         } else {
            $defaultValue = undef;
         }
         
         $method = $package . "::" . $potentialAccessor;
         next if defined( &{$method} );  # subroutine already exists...

      eval
      {
         if( defined( $defaultValue ) )
         {
            *$method = sub { 
               use strict;
               my $self = shift;
               $self->accessor( $potentialAccessor, $defaultValue ) unless defined( $self->accessor( $potentialAccessor ) );
               return( $self->accessor( $potentialAccessor, @_ ) );
            }
         }
         else
         {
            *$method = sub { 
               use strict;
               my $self = shift;
               return( $self->accessor( $potentialAccessor, @_ ) );
            }
         }
      }
   }
}
# ---------------------------------------------------------------------- #
sub createBooleanAccessors
{
   use strict;
   my $self = shift;
   return $self->createBooleanAccessorsWithIndex( 2, @_ );
}
# ---------------------------------------------------------------------- #
sub createBooleanAccessorsWithIndex
{
   use strict;
   no strict 'refs';
   my $self = shift;
   my $callerIndex = shift;
   my @potentialAccessors = @_;
   my @info = caller($callerIndex);
   my $package = $info[0];
   my @buffer;
   my $defaultValue;
   my $method;
   
   foreach my $potentialAccessor (@potentialAccessors)
   {

      if( ref( $potentialAccessor ) eq 'ARRAY' )
      {
         @buffer = @{ $potentialAccessor };
         $potentialAccessor = $buffer[0];
         $defaultValue      = $buffer[1];
      } else {
         $defaultValue = undef;
      }
 
      $method = $package . "::" . $potentialAccessor;

      next if defined( &{$method} );  # subroutine already exists...

      eval
      {
         *$method = sub { 
            use strict;
            my $self = shift;
            if( $#_ >= 0 and $_[0] =~ /(1|true)/i ){
               $self->accessor( $potentialAccessor, 1 );
            } elsif( $#_ >= 0 and $_[0] =~ /(0|false)/i ){
               $self->accessor( { '-name' => $potentialAccessor, '-delete' => 1 } );
            }
            return( $self->accessor( $potentialAccessor ) );
         }
      }
   }
}
# ---------------------------------------------------------------------- #
sub addVariables
{
   use strict;
   no strict 'refs';
   my $self = shift;
   my $variableName;

   return undef unless defined( $_[0] );

   $self->createAccessors( @_ );

   foreach (@_)
   {
      if( ref( $_ ) eq 'ARRAY' and defined( $_->[1] ) )
      {
         eval
         {
            $variableName = $_->[0];
            $self->$variableName( $_->[1] );
         }
      } 
   }

   return( 1 );
}
# ---------------------------------------------------------------------- #
sub addBooleanVariables
{
   use strict;
   no strict 'refs';
   my $self = shift;
   my $variableName;

   return undef unless defined( $_[0] );

   $self->createBooleanAccessors( @_ );

   foreach (@_)
   {
      if( ref( $_ ) eq 'ARRAY' and defined( $_->[1] ) )
      {
         eval
         {
            $variableName = $_->[0];
            $self->$variableName( $_->[1] );
         }
      }
   }

   return( 1 );
}
# ---------------------------------------------------------------------- #
sub hAll
{
   use strict;
   my $self = shift;
   my %buffer;

   foreach $_ ( $self->accessor() )
   {
      $buffer{ $_ } = $self->accessor( $_ );
   }
   return %buffer;
}
# ---------------------------------------------------------------------- #
sub createAliases
{
   use strict;
   no strict 'refs';
   my $self = shift;
   my @potentialAccessors = @_;
   my @info = caller(0);
   my $package = $info[0];
   my @buffer;
   my $alias;
   my $method;
   
   foreach my $potentialAccessor (@potentialAccessors)
   {

      @buffer = @{ $potentialAccessor };
      $alias  = $package."::".$buffer[0];
      $method = $package."::".$buffer[1];
     
      next if defined( &{$alias} );  # subroutine already exists...

      eval
      {
         *$alias = \&$method;
      };
   }
}
1;
__END__
########################################
=head1 NAME

Obect - Base Class to build objects from

=head1 SYNOPSIS

See Description

=head1 DESCRIPTION

This module describes a perl object based upon an anonymous hash.  It is 
most useful as the basis for creating other types of objects.  

The new() method creates a new perl object.  The code is copied almost 
verbatim out of O'Reilly's Perl Cookbook.  Any pass parameters are then 
funneled into the _Init() subroutine.  Thus, when inheriting the 
properties of this object, the _Init() function can be re-written without 
having to worry about the details of blessing the new object.  

The accessor method is based upon the CGI param method.  It is useful for 
adding or retrieving information from the module without worrying about if 
the thing is an array, hash, scalar, ...  

=head2 EXAMPLE: Inheriting from Object

Consider a package called 'necklace'.  Each necklace will have an 
associated price and description.  The package 'Necklace' can be declared 
as: 

   package Necklace;
   use strict;
   use vars qw(@ISA @EXPORT @EXPORT_OK %EXPORT_TAGS $VERSION);
   use Exporter;
   use Object;
   $VERSION = '1.00';
   @ISA = ('Exporter', 'Object');
   @EXPORT = ( );
   @EXPORT_OK = qw();
   %EXPORT_TAGS = ();

In another script the necklace package could be used:

   use Necklace;
   $necklace = 'Necklace'->new;
   
Now, to start saving and accessing information from the necklace, we could 
create a method in the necklace package called sPrice: 

   sub sPrice
   {
      use strict;
      my $self = shift;
      my $key  = 'PRICE';
      return( $self->accessor($key, @_) );
   }
   
To assciate the price with the necklace, we write:

   $necklace->sPrice( 20.00 );
   
To retrieve the price, we can call:

   $value = $necklace->sPrice;
   
The value of the accessor function is being able to store and retrieve hashes 
and arrays simply.  

In the case a default value is wanted, an adjustment can be made to the 
sPrice method: 

   sub sPrice
   {
      use strict;
      my $self    = shift;
      my $key     = 'PRICE';
      my $default = 20.00;

      $self->accessor( $key, $default ) unless( $self->accessor( $key ) );

      return( $self->accessor($key, @_) );
   }
   
Another way to default the value is by overriding the _Init() method: 

   sub _Init()
   {
      my $self = shift;
      my @args = @_;  # all the pass parameters sent to new()
                      # are passed along to _Init()
   
      #set up a default price of $20.00
      $self->sPrice( 20.00 );
   }

If we also wanted to clear the price from the object another modification 
may be made such that a special value of '-clear' will cause the parameter 
'PRICE' to be dropped from the object's anonymous hash: 
   
   sub sPrice
   {
      use strict;
      my $self    = shift;
      my $key     = 'PRICE';
      my $default = 20.00;
   
      $self->accessor( $key, $default ) unless( $self->accessor( $key ) );
      
      # using eq '-clear' rather than =~ /-clear/
      # because I speculate the eq is faster.
      if( $#_ >= 0  and $_[0] eq '-clear' )
      {
         $self->accessor( { '-name' => $key, '-delete' => 1 } );
      }
      elsif( $#_ >= 0 )      
      {
         $self->accessor( $key, @_ );
      }

      return( $self->accessor($key) );
   }

Creating accessors of this style is such a common thing, that there is a 
subroutine to create them automatically: createAccessors(); 

   sub _Init()
   {
      my $self = shift;
      my @args = @_;  # all the pass parameters sent to new()
                      # are passed along to _Init()
   
      #set up a default price of $20.00
      $self->createAccessors( 'sPrice' );
      $self->createAccessors( [ 'sPrice2', 20] );
   }

createAccessors takes a list of strings or array references.  An array 
reference should indicate the accessor name and the default value.  
Another routine, addVariables, will create the accessor and call it once 
such that the key/value is loaded into the blessed hash.  The routine 
$self->hAll will return all the key/values loaded in the blessed hash.  

=head1 METHODS

=head2 $object = 'Object'->new( @_ )

=over 4

This function creates an anonymous hash and blesses it into an object.  
copied from the O'Reilly Perl Cookbook 

=back

=head2 _Init()

=over 4

Automatically called by the new method.  Should not be called directly.  
This default method is trivial placeholder designed to be overridden.  

The new() method will call _Init() and pass along all the arguments.  A 
module which bases itself upon the object package can overwrite this 
method, _Init(), to create a customized initilization routine without 
having to alter the new() method.  

=back

=head2 accessor()

=over 4

Useage:

    @keys  = $object->accessor();
    $value = $object->accessor( $key );  
    @value = $object->accessor( $key );
    $object->accessor( $key, $value );   
    $object->accessor( $key, @value );   
    $object->accessor( $key, \@value );   
    $object->accessor( $key, \%value );   
    $object->accessor( { '-name'=>$key, '-value'=>'value'} );   
    $object->accessor( { '-name'=>$key, '-delete'=>'1'} );

This function is modeled after the accessor function in the CGI.pm module.  
It's goal is to store and retrieve values from within an object 

Given no arguments the function will return a list of all available 
accessors 

 ex: $object = 'object'->new();
     @keys   = $object->accessor();

Given a single argument, the function will return the value saved in the 
object associate with that hash.  If called in a scalar context a scalar 
value will be returned.  If the stored information belongs to a hash or 
array, a reference to the hash or array will be returned.  If called in 
array context, an array will be returned.  

To store a value there are a number of methods.  First, calling the 
function with two parameters will save them as key=value pairs.  
 
 ex: $object->accessor( 'key', 'value' );

If an array is to be saved, the function can be called in one of two ways 
 
 ex: $object->accessor( 'key', 'value1', 'value2', ... );
 or: $object->accessor( 'key', \@values );

A hash needs to be saved by the following technique 
 
 ex: $object->accessor( 'key', \%hash );

An alternaive method of passing values into the function is with a hash 
reference 
 
 ex: $object->accessor( { '-name ' => 'key'
                       '-value' => 'value' } );

To delete a stored value, use the following (the presence of the delete 
parameter is enough to delete it; the value does not matter 
 
 ex: $object->accessor( {'-name'   => 'key',
                      '-delete' => 1 } );

Known Bugs:

When saving a single hash reference, it will lose the distinction of being 
an array reference and depending on the context will be returned as an 
array or an array reference.  

=back

=head2 $object->createAccessors( @methods );

=over 4

This function is used to create a generic accessor method which uses the 
accessor function.  It's supplied to save a lot of typing.  

C<$object->createAccessors( 'helloWorld' );> will essentially create a 

sub routine like the following: 

   sub helloWorld
   {
      use strict;
      my $self = shift;
      return( $self->accessor( 'helloWorld', @_ ) );
   }

To set a default value, use $object->createAccessors( [ 'helloWorld', "hello world" ] );

Now you can use $object->helloWorld( @_ ); as well as
$object->accessor( 'helloWorld', @_ );

If the method already exists in the module, nothing will be done.

=back

=head2 $object->addVariable( @variableNames );

=over 4

@variableNames is either a string or [ 'variableName', $defaultValue' ]

This method will create the accessor method if it doesn't exist and call 
it once to initialize the default value.  Since Object is a blessed hash, 
the key value doesn't get written into the hash until the accessor is 
called.  In other words, hAll won't show key/value pairs for variables 
which haven't had their accessors called yet.  The method returns 1 if 
successful, undefined otherwise.  

A common usage is in the _Init routine to create and initialize accessors.  

=back

=head2 %parameters = $object->hAll();

=over 4

This function will return a hash of all the key/value pairs in the 
object's blessed hash.  

=back

=head1 AUTHOR

=over4

=item Scott Selberg

=item April 24, 2006

=item $Revision: 1.3 $

=back

=head1 COPYRIGHT

Copyright 4/19/2006, Keysight Technologies.

This program is free software.  You may copy,
modify, or redistribute it under the same terms
as Perl itself.

=cut
