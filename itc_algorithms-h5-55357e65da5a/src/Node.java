import java.util.*;

public class Node {
   private String name;
   private Node firstChild;
   private Node nextSibling;

   Node(String n, Node d, Node r) {
      name = n;
      firstChild = d;
      nextSibling = r;
   }

   public static Node parsePostfix(String s) {
      try {
         if (!s.contains("(") || !s.contains(")")) {
            if (s.trim().matches("[a-zA-Z0-9.]+")) {
               return new Node(s.trim(), null, null);
            }
            throw new RuntimeException("Invalid input string: missing parentheses in " + s);
         }

         if (s.contains("( ,") || s.contains(", )")) {
            throw new RuntimeException("Invalid input string: comma between parentheses in " + s);
         }

         int[] idx = new int[]{0};
         Node root = parsePostfixHelper(s, idx, false);
         if (idx[0] != s.length()) {
            throw new RuntimeException("Invalid input string in " + s);
         }
         return root;
      } catch (RuntimeException e) {
         throw new RuntimeException("Error in parsePostfix: " + e.getMessage());
      }
   }

   private static Node parsePostfixHelper(String s, int[] idx, boolean allowExtraComma) {
      if (idx[0] >= s.length()) {
         throw new RuntimeException("Unexpected end of input string in " + s);
      }

      Node root = null;

      while (idx[0] < s.length() && s.charAt(idx[0]) != ',' && s.charAt(idx[0]) != ')') {
         char c = s.charAt(idx[0]);
         idx[0]++;

         if (c == '(') {
            if (root == null) {
               root = new Node(null, null, null);
            } else if (root.firstChild != null) {
               throw new RuntimeException("Invalid input string: multiple children for the same node in " + s);
            }
            root.firstChild = parsePostfixHelper(s, idx, true);
         } else {
            StringBuilder nodeName = new StringBuilder();
            nodeName.append(c);

            while (idx[0] < s.length() && s.charAt(idx[0]) != ',' && s.charAt(idx[0]) != ')' && s.charAt(idx[0]) != '(') {
               if (!Character.isLetterOrDigit(s.charAt(idx[0])) && s.charAt(idx[0]) != ' ') {
                  throw new RuntimeException("Invalid character in node name in " + s);
               }
               nodeName.append(s.charAt(idx[0]));
               idx[0]++;
            }

            if (root == null) {
               root = new Node(nodeName.toString(), null, null);
            } else if (root.name != null) {
               throw new RuntimeException("Invalid input string: multiple names for the same node in " + s);
            } else {
               root.name = nodeName.toString();
            }
         }
      }

      if (root == null) {
         throw new RuntimeException("Invalid input string: empty node name in " + s);
      }

      if (!allowExtraComma && idx[0] < s.length() && s.charAt(idx[0]) == ',') {
         throw new RuntimeException("Invalid input string: extra comma in " + s);
      }

      if (idx[0] < s.length() && s.charAt(idx[0]) == ')') {
         idx[0]++;
      }

      // Connect siblings
      if (idx[0] < s.length() && s.charAt(idx[0]) == ',') {
         idx[0]++;
         if (root == null) {
            throw new RuntimeException("Invalid input string: sibling without a parent in " + s);
         }
         root.nextSibling = parsePostfixHelper(s, idx, true);
      }

      if (root.name.equals(",")) {
         throw new RuntimeException("Invalid input string: comma between parentheses in " + s);
      }

      if (idx[0] < s.length() && s.charAt(idx[0]) == ',') {
         throw new RuntimeException("Invalid input string: unexpected comma in " + s);
      }

      return root;
   }

   public String leftParentheticRepresentation() {
      StringBuilder sb = new StringBuilder();
      leftParentheticRepresentationHelper(this, sb);
      return sb.toString();
   }

   private void leftParentheticRepresentationHelper(Node node, StringBuilder sb) {
      if (node == null) {
         return;
      }

      sb.append(node.name);

      if (node.firstChild != null) {
         sb.append("(");
         leftParentheticRepresentationHelper(node.firstChild, sb);
         sb.append(")");
      }

      if (node.nextSibling != null) {
         sb.append(",");
         leftParentheticRepresentationHelper(node.nextSibling, sb);
      }
   }



   public static void main (String[]param){
      // Example from prompt
      String s = "(B1,C)A";
      Node t = Node.parsePostfix(s);
      String v = t.leftParentheticRepresentation();
      System.out.println(s + " ==> " + v); // (B1,C)A ==> A(B1,C)

      // Test case 1
      s = "(B1,C,D)A";
      t = Node.parsePostfix(s);
      v = t.leftParentheticRepresentation();
      System.out.println(s + " ==> " + v); // (B1,C,D)A ==> A(B1,C,D)

      // Test case 2
      s = "(((2,1)-,4)*,(69,3)/)+";
      t = Node.parsePostfix(s);
      v = t.leftParentheticRepresentation();
      System.out.println(s + " ==> " + v); // (((2,1)-,4)*,(69,3)/)+ ==> +(*(-(2,1),4),/(69,3))
   }

}