package org.egomez.irpgeditor;

/**
 * @author not attributable
 */
public class LayoutRequest {
  AS400System system;
  String name;
  String parsedName, parsedSchema;
  
  public LayoutRequest(AS400System system, String name) {
    this.system = system;
    this.name = name;
    parse();
  }
  
  public LayoutRequest(String name) {
    this(null, name);
    parse();
  }
  
  protected void parse() {
    int index;
    
    index = name.indexOf("/");
    if ( index == -1 ) {
      parsedName = name.toUpperCase();
      parsedSchema = null;
    }
    else {
      parsedSchema = name.substring(0, index).toUpperCase();
      parsedName = name.substring(index + 1, name.length()).toUpperCase();
    }
  }
  
  public String getParsedName() {
    return parsedName;
  }
  
  public String getSchema() {
    return parsedSchema;
  }
  
  public void setSchema(String schema) {
    this.parsedSchema = schema;
  }
  
  public String getName() {
    return name;
  }
  
  public AS400System getSystem() {
    return system;
  }
  
  public int hashCode() {
    return name.hashCode();
  }
  
  public boolean equals(Object object) {
    if ( object == null ) {
      return false;
    }
    if ( object instanceof LayoutRequest ) {
      LayoutRequest lr = (LayoutRequest)object;
      if ( lr.name.equalsIgnoreCase(name) == false ) {
        return false;
      }
      if ( lr.system != system ) {
        return false;
      }
      return true;
    }
    return false;
  }
}
