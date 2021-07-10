package unifiedResolution;

/**
 * @author Windows Vikram Manjare (CS20M070)
 *
 * 02-May-2021
 */

public class Display {
	public static enum LineType {
		  SOLID {
		      public String toString() {
		          return "_";
		      }
		  },

		  DASHED {
		      public String toString() {
		          return "-";
		      }
		  },
		  
		  STAR {
			  public String toString() {
		          return "*";
		      }
		  }
		}
	
	public static void printLine(LineType lineType) {
		for(int i=0; i<80; i++) {
			System.out.print(lineType);
		}
		System.out.println();
	}
}
