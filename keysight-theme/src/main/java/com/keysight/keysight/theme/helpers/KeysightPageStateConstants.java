package com.keysight.keysight.theme.helpers;
import java.util.Map;
import java.util.HashMap;

import com.atlassian.confluence.labels.Label;

public class KeysightPageStateConstants
{
   public static final String PAGE_STATE_ACTIVATION_KEY = "documentation";

   // This stuff is all replicated in the javascript.  I couldn't 
   // figure out how to get the strings there from here.
   public static final String NOTE_KEY       = "note";
   public static final String DRAFT_KEY      = "draft";
   public static final String REVIEWED_KEY   = "reviewed";
   public static final String FINALIZED_KEY  = "finalized";
   public static final String HAS_ERRORS_KEY = "has-errors";
   public static final String INCOMPLETE_KEY = "incomplete";
   public static final String DEPRECATED_KEY = "deprecated";
   
   public static final String NOTE_VALUE       = "Note";
   public static final String DRAFT_VALUE      = "Draft";
   public static final String REVIEWED_VALUE   = "Reviewed";
   public static final String FINALIZED_VALUE  = "Finalized";
   public static final String HAS_ERRORS_VALUE = "Has Errors";
   public static final String INCOMPLETE_VALUE = "Incomplete";
   public static final String DEPRECATED_VALUE = "Deprecated";
   
   // use a map to keep the relationship between the key and displayed text
   public static final Map<String, String> PAGE_STATES = new HashMap<String, String>();
   static {
      PAGE_STATES.put( NOTE_KEY,       NOTE_VALUE );
      PAGE_STATES.put( DRAFT_KEY,      DRAFT_VALUE );
      PAGE_STATES.put( REVIEWED_KEY,   REVIEWED_VALUE );
      PAGE_STATES.put( FINALIZED_KEY,  FINALIZED_VALUE );
      PAGE_STATES.put( HAS_ERRORS_KEY, HAS_ERRORS_VALUE );
      PAGE_STATES.put( INCOMPLETE_KEY, INCOMPLETE_VALUE );
      PAGE_STATES.put( DEPRECATED_KEY, DEPRECATED_VALUE );
   }

   // use an array to keep the key order
   public static final String[] PAGE_STATE_KEYS = { NOTE_KEY,
	                                            DRAFT_KEY,
						    REVIEWED_KEY,
						    FINALIZED_KEY,
						    HAS_ERRORS_KEY,
						    INCOMPLETE_KEY,
						    DEPRECATED_KEY };

   public static boolean isPageStateOrActivationLabel( Label label ){
      return isPageStateOrActivationLabel( label.getName() );
   }

   public static boolean isPageStateOrActivationLabel( String label ){
      boolean bFlag = false;
      
      if( PAGE_STATE_ACTIVATION_KEY.equals( label ) ){
         bFlag = true;
      } else if( isPageStateLabel( label ) ){
         bFlag = true;
      }
 
      return( bFlag );
   }

   public static boolean isPageStateLabel( Label label ){
      return isPageStateLabel( label.getName() );
   }
   
   public static boolean isPageStateLabel( String label ){
      boolean bFlag = false;
      
      for( String pageStateLabel : PAGE_STATES.keySet() ){
         if( pageStateLabel.equals( label ) ){
            bFlag = true;
            break;
         }
      }

      return bFlag; 
   }
}
