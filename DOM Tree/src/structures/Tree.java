package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		Stack<TagNode> tagStack=new Stack<TagNode>();
		root=new TagNode("html",null,null);
		sc.nextLine();
		tagStack.push(root);
		while (sc.hasNext()){
			String temp=sc.nextLine();
			Boolean Tag=false;
			if (temp.charAt(0)=='<'){
				if (temp.charAt(1)=='/'){
					tagStack.pop();
					continue;
				}
				else{
					temp=temp.replace(">","");
					temp=temp.replace("<","");
					Tag=true;
				}
			}
			TagNode node=new TagNode(temp, null, null);
			if (tagStack.peek().firstChild==null) {
				tagStack.peek().firstChild=node; 
			}
			else{
				TagNode traverse=(tagStack.peek()).firstChild;
				while (traverse.sibling != null){
					traverse=traverse.sibling;
				}
				traverse.sibling=node;
			}
			if (Tag==true){
				tagStack.push(node);
			}
		}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		if (root==null || oldTag==null || newTag==null) {
			System.out.println("Tags not found");
			return;
		}
		else {
			recursiveReplaceTag(oldTag,newTag,root);
		}
	}
	private void recursiveReplaceTag(String oldTag,String newTag,TagNode root) {
		if (root==null) {
			return;
		}
		if ((root.tag.equals(oldTag)) && (root.firstChild!=null)) {
			root.tag=newTag;
		}
		recursiveReplaceTag(oldTag,newTag,root.firstChild);
		recursiveReplaceTag(oldTag,newTag,root.sibling);
	}
	
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		if (row<1) {
			System.out.println("No table found");
			return;
		}
		else {
		root=recursiveBoldRow(row,root);
		}
	}
	
	private TagNode recursiveBoldRow(int row,TagNode root){
		if(root==null) {
			return null;
		}
		
		root.firstChild=this.recursiveBoldRow(row,root.firstChild);
		if(root.tag.equals("table")){
			TagNode tableRow=root.firstChild;
			for(int i=0;i<row-1;i++) {
				if (tableRow.sibling!=null) {
					tableRow=tableRow.sibling;
				}
			}
			TagNode tableColumn=tableRow.firstChild;
			while(tableColumn!=null){
				TagNode temp=new TagNode("b",tableColumn.firstChild,null);
				tableColumn.firstChild=temp;
				tableColumn=tableColumn.sibling;
			}
		}
		root.sibling=recursiveBoldRow(row,root.sibling);
		return root;
	}
	
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		if (tag.contentEquals("b") || tag.contentEquals("em") || tag.contentEquals("p")) {
			removePEmBRecursively(tag,root);
		}
		if (tag.equals("ol") || tag.equals("ul")) {
			removeOlUlRecursively(tag,root);
		}
	}
	private void removeOlUlRecursively(String tag2,TagNode root) {
		TagNode ptr;
		if (root==null) {
			return;
		}
		if (root.firstChild!=null && (root.tag.equals(tag2))){
			root.tag="p";
			if (root.firstChild.sibling!=null) {
				for (ptr=root.firstChild;ptr.sibling!=null;ptr=ptr.sibling) {
					ptr.tag="p";
				}
				ptr.tag = "p";
				ptr.sibling = root.sibling;
				root.sibling = root.firstChild.sibling;
				root.firstChild = root.firstChild.firstChild;
			}
		}
		removeOlUlRecursively(tag2,root.firstChild); 
		removeOlUlRecursively(tag2,root.sibling);
	}
	
	
	
	private void removePEmBRecursively(String tag2,TagNode root) {
		TagNode ptr;
		if (root==null) {
			return;
		}
		if (root.firstChild!=null && root.tag.equals(tag2)) {
			root.tag=root.firstChild.tag;
			if (root.firstChild.sibling!=null) {
				for (ptr=root.firstChild;ptr.sibling!=null;ptr=ptr.sibling) {
					ptr.sibling=root.sibling;
				}
				root.sibling=root.firstChild.sibling;
			}
			root.firstChild=root.firstChild.firstChild;
		}
			removePEmBRecursively(tag2,root.sibling);
			removePEmBRecursively(tag2,root.firstChild);
	}
	
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		if (root==null ||word==null ||tag==null) {
			return;
		}
		if (tag.contentEquals("em") || tag.equals("b")) {
			recursiveAdd(root,word.toLowerCase(),tag);
		}
	}
	
	private void recursiveAdd(TagNode root,String word2,String tag2) {
		if(root==null) {
			return; 
		}
		recursiveAdd(root.firstChild,word2,tag2);
		recursiveAdd(root.sibling,word2,tag2);
		if(root.firstChild==null) {
			while(root.tag.toLowerCase().equals(word2)) {
				int i=0;
				Boolean wordFound=false;
				String wordString="";
				String[] wordArray=root.tag.split(" ");
				StringBuilder sb=new StringBuilder(root.tag.length());
				for (i=0;i<wordArray.length;i++) {
					if(wordArray[i].toLowerCase().matches(word2+"[.?!,]?")) {
						wordFound=true;
						wordString = wordArray[i];
						int x=0;
						for(x=i+1;x<wordArray.length;x++) {
							sb.append(wordArray[x]+" ");
						}
						break;
					}
				}
				if(wordFound==false) {
					return;
				}
				
				String tagTrim=sb.toString().trim();
				
				if(i==0) { 
					root.tag=tag2;
					root.firstChild=new TagNode(wordString,null,null);
					if(tagTrim.equals("")==false) { 
						root=root.sibling;
						root.sibling=new TagNode(tagTrim,null,root.sibling);
					}
				}
				else { 
					TagNode temp=new TagNode(wordString, null, null);
					TagNode temp2=new TagNode(tag2,temp,root.sibling);
					root.sibling=temp2;
					root.tag=root.tag.replaceFirst(wordString,"");
					if(tagTrim.equals("")==false) {
						root.tag=root.tag.replace(tagTrim, "");
						temp2.sibling=new TagNode(tagTrim,null,temp2.sibling);
						root=temp2.sibling;
					}
				}
			} 
		}
	}
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
