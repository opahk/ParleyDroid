/**
 * Copyright (C) 2008 Michael Hofer (mobvoc [at] unglaublich.priv.at)

 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package com.binaryelysium.android.parley.data;

import java.util.Hashtable;
import java.util.GregorianCalendar;


/**
 * @author michhof
 *
 */
public class Entry {
  private String mId;  
  private Hashtable mTranslations; //key: Translation.id; value: Word object
  private int mLevel; //counts how often this entry was known
  private GregorianCalendar mDate;
  private VocSet mVocSet;

  public Entry(String entryId, VocSet vocSet) {
    mId = entryId;
    mTranslations = new Hashtable();
    mLevel = 0; //start in level 0 (never known)
    mVocSet = vocSet;
  }

  public Word addTranslation(String id) {
    Word w = new Word(id, mVocSet);
    mTranslations.put(id, w);
    return w;
  }

  public Word getWord(String translationId) {
    return (Word)mTranslations.get(translationId);
  }

  public void setLevel(int level) {
	  //System.out.println( "Setting level for Entry");
	  ((Word)mTranslations.get("1")).setLevel(level);
	  //System.out.println( "Setting level for Entry: completed");
  }  
  
  public int getLevel() {
	int level = 0;
	//System.out.println( "Entry.getLevel()");
	level = ((Word)mTranslations.get("1")).getLevel();
	//System.out.println( "Entry.getLevel(): ready to return");
	return level;
  }

  public boolean isExaminable() {
	  int level = 0;
	  long DAY = 86400;
	  long difference = 0;
	  long threshold = 0;
	  GregorianCalendar now = new GregorianCalendar();
      difference = (long) (now.getTime().getTime() - (this.getWord("1").getDate()).getTime().getTime())/1000;

      level = this.getLevel();
      if(level==0) threshold = 0;
      else if (level==1) threshold = 1 * DAY;
      else if (level==2) threshold = 2 * DAY;
      else if (level==3) threshold = 4 * DAY;
      else if (level==4) threshold = 7 * DAY;
      else if (level==5) threshold = 14 * DAY;
      else if (level==6) threshold = 30 * DAY;
      
      
      System.out.println( "difference " + difference + ", threshold " + threshold + " and level " + level);

      if (difference > threshold) return true;
      else return false;
  }
}
