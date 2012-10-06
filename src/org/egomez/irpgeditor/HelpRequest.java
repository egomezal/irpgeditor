package org.egomez.irpgeditor;

/**
 * @author not attributable
 */

public class HelpRequest {
  String word;
  
  public HelpRequest(String word) {
    this.word = word;
  }
  
  public String getWord() {
    return word;
  }
  
  public int hashCode() {
    return word.hashCode();
  }
  
  public boolean equals(Object object) {
    if ( object == null ) {
      return false;
    }
    return word.equals(object.toString());
  }
}
