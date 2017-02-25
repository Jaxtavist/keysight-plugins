#!/usr/bin/perl -w
use strict;
use MIME::Base64;
my $username;
my $password;
my $credentials;
my $i = 1;
my $credentialsFileBase = 'credentials';
my $credentialsFile;

print( "Enter the username:" );
$username = <STDIN>;
chomp $username;

print( "Enter the password:" );
$password = <STDIN>;
chomp $password;

print $username.";".$password . "\n";

$credentials = encode_base64( $username . ":" . $password );

$credentialsFile = "$credentialsFileBase.txt";
while( -e $credentialsFile ){
   $credentialsFile = "$credentialsFileBase.$i.txt";
   $i++;
}

if( open( FILE, ">$credentialsFile" ) ){
   print FILE "$credentials";
   close FILE;
   print "Created $credentialsFile\n";
} else {
   print "Failed to create $credentialsFile\n";
}

   
