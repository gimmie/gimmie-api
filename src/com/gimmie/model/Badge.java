package com.gimmie.model;

import com.gimmie.Configuration;
import com.gimmie.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Badge model returns from Gimmie service in
 * {@link com.gimmie.Gimmie#loadBadges(com.gimmie.AsyncResult, boolean))} api.
 * 
 * @author llun
 * 
 */
public class Badge extends IDBaseObject {
  
  public Badge(JSONObject object, Configuration configuration) {
    super(object, configuration);
  }

  public String getName() {
    return mObject.optString("name");
  }

  public String getDescription() {
    return mObject.optString("description");
  }

  public String getImageURL() {
    String imageURL = mObject.optString("image_url_retina");

    if (imageURL.startsWith("//")) {
      return String.format("http:%s", imageURL);
    } else if (imageURL.startsWith("/")) {
      return String.format("http://gm.llun.in.th:3000%s", imageURL);
    } else {
      return imageURL;
    }
  }
  
  /** 
   * @return orRules, as an ArrayList<ArrayList<String> in the structure:
   *  (A&B)||(C&&D)||(E)...
   */
  public ArrayList<ArrayList<String>> getRulesStringArray(){
	  ArrayList<ArrayList<String>> orRules = new ArrayList<ArrayList<String>>();
	  try {
		  JSONArray orRulesRaw = mObject.optJSONArray("or");
		  if(orRulesRaw!=null){
			  for(int i=0; i<orRulesRaw.length(); i++) {
				  JSONArray andRulesRaw;
				
				  andRulesRaw = orRulesRaw.getJSONArray(i);
				
				  ArrayList<String> andRule = new ArrayList<String>();
				  if(andRulesRaw!=null){
					  for(int j=0; j<andRulesRaw.length(); j++){
						  BadgeRule badgeRule = new BadgeRule(andRulesRaw.getJSONObject(j), mConfiguration);
						  andRule.add(badgeRule.toString());
						  Logger.getInstance().debug("and rule", badgeRule.toString());
					  }
				  }//end if
				  orRules.add(andRule);
			  }
		  }//end if
	  } catch (JSONException e) {
			e.printStackTrace();
		}
	  return orRules;
  }
  
  public String badgeRuleToString(){
	  String orRules = "";
	  try {
		  JSONArray orRulesRaw = mObject.optJSONObject("rule_description")
				  .optJSONArray("or");
		  if(orRulesRaw!=null){
			  for(int i=0; i<orRulesRaw.length()-1; i++) {
				  JSONObject andRulesRaw;
				
				  andRulesRaw = orRulesRaw.getJSONObject(i);
				
				  String andRule = "";
				  if(andRulesRaw!=null){
					  for(int j=0; j<andRulesRaw.length()-1; j++){
						  BadgeRule badgeRule = new BadgeRule(
								  andRulesRaw.getJSONArray("and").getJSONObject(j)
								  , mConfiguration);
						  andRule += badgeRule.toString() + " AND ";
              Logger.getInstance().debug("and rule", badgeRule.toString());
					  }
					  //the last AND rule:
					  BadgeRule badgeRule = new BadgeRule(
							  andRulesRaw.getJSONArray("and").getJSONObject(andRulesRaw.length()-1)
							  , mConfiguration);
					  andRule += badgeRule.toString();
					  Logger.getInstance().debug("and rule", badgeRule.toString());
				  }//end if
				  orRules += andRule + " OR ";
			  }
			  
			  //the last OR rule:
			  JSONObject andRulesRaw;
			  andRulesRaw = orRulesRaw.getJSONObject(orRulesRaw.length()-1);
			  String andRule = "";
			  if(andRulesRaw!=null){
				  for(int j=0; j<andRulesRaw.length()-1; j++){
					  BadgeRule badgeRule = new BadgeRule(andRulesRaw.getJSONArray("and").getJSONObject(j), mConfiguration);
					  andRule += badgeRule.toString() + " AND ";
            Logger.getInstance().debug("and rule", badgeRule.toString());
				  }
				  //the last AND rule:
				  BadgeRule badgeRule = new BadgeRule(
						  andRulesRaw.getJSONArray("and").getJSONObject(
								  andRulesRaw.length()-1), mConfiguration);
				  andRule += badgeRule.toString();
          Logger.getInstance().debug("and rule", badgeRule.toString());
			  }//end if
			  orRules += andRule;
		  }//end if
	  } catch (JSONException e) {
			e.printStackTrace();
		}
	  return orRules;
  }
  
  public String getCategory(){
	  return mObject.optString("category_name");
  }
  
  public String getTier(){
	  return mObject.optString("tier");
  }

  public String getUnlockMessage(){
	  return mObject.optString("unlock_message");
  }
  
  
  /**
   * BadgeRules which contains an {@link Event}, 
   * and its target (the "at_least" value)
   * and/or its progress by current user
   * 
   * for future native display of Badges
   */
  private static class BadgeRule extends IDBaseObject{
	    
		public BadgeRule(JSONObject object, Configuration configuration) {
			super(object, configuration);
		}
		
		/**
		 * @return description for the badge rule.
		 * Helper when printing badge rule 
		 * 
		 */
		public String toString(){
			String verb = mObject.optString("verb");
			String capVerb = verb.substring(0, 1).toUpperCase(Locale.US) + verb.substring(1);
			return capVerb + " "
					+ mObject.optInt("at_least") + " "
					+ mObject.optString("noun");
		}
	}
}



