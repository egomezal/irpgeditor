package org.egomez.irpgeditor.flowchart;

/**
 * @author not attributable
 */
public class FCOp {
  String text;
  int start, end;
  String op;
  
  public FCOp(String op, String text, int start, int end) {
    this.text = text;
    this.op = op;
    this.start = start;
    this.end = end;
  }
  
  public String getOp() {
    return op;
  }
  
  public String getText() {
    return text.substring(start, end).trim();
  }
  
  public int getStart() {
    return start;
  }
  
  public int getEnd() {
    return end;
  }
}
