import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

//TODO: Book folders need to remove (3-5 numbers (so far)) in their names
//TODO: Book folders and files need to have _ removed
//TODO: Files need to be renamed to capitalize important words
//TODO: Folders and Files get names cut short - most likely manual fix // Seems to be books with _ in them

//TODO: Button to capitalize book names
//TODO: Either a refresh button or a method that refreshes the dir listing pane after renaming
//TODO: Maybe create a text file after running that lists the names of the files that were modified
//TODO: Process button for seeing what WILL change but not actually make the change 
//TODO: Are you sure popup on the rename button that calls process so you know how many files you will be renaming

public class eBookRenamer {

	JFrame frame = new JFrame(System.getProperty("user.name") + " :: "
			+ this.getClass().getName());
	JButton recursiveOpen = new JButton("Open Recursively");
	JButton dynamicOpen = new JButton("Open Dynamically");
	JButton removeAllParens = new JButton("Remove All ()");
	JButton removeAllUnderscore = new JButton("Remove All _");
	JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home")
			+ "\\Desktop");
	File folder = new File(System.getProperty("user.home") + "\\Desktop");
	int pCount = 0;
	int uCount = 0;

	public eBookRenamer() {
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
		JPanel leftPane = new JPanel();
		JPanel rightPane = new JPanel();
		//TODO: Fix layouts to fit what I need

		rightPane.setLayout(new GridLayout(4, 1));

		leftPane.add(new RecursiveTree(folder));
		rightPane.add(recursiveOpen);
		rightPane.add(dynamicOpen);
		rightPane.add(removeAllParens);
		rightPane.add(removeAllUnderscore);

		splitPane.add(leftPane);
		splitPane.add(rightPane);

		frame.add(splitPane);

		init();
	}

	private void init() {
		//TODO: Organize in correct order
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Standard close action
		//frame.setAlwaysOnTop(true); // Set's the window to be "always on top"
		frame.pack();
		frame.setLocationRelativeTo(null);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		setUpListeners();

		frame.setVisible(true);
	}

	private void setUpListeners() {
		ActionListener recursiveDir = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					folder = fileChooser.getSelectedFile();
				}
			}
		};
		recursiveOpen.addActionListener(recursiveDir);

		ActionListener dynamicDir = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					folder = fileChooser.getSelectedFile();
				}
			}
		};
		dynamicOpen.addActionListener(dynamicDir);

		ActionListener removeParens = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (folder != null) {
					walkin(folder);
				}
				System.out.println("Total Renamed: " + pCount);
				pCount = 0;
			}
		};
		removeAllParens.addActionListener(removeParens);

		ActionListener removeUndrerscores = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO replace underscore with " -"
			}
		};
		removeAllUnderscore.addActionListener(removeUndrerscores);
	}

	private void walkin(File dir) {
		File listFile[] = dir.listFiles();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				if (listFile[i].isDirectory()) {
					if (listFile[i].getName().contains("(")
							&& listFile[i].getName().contains(")")) {
						String newName = listFile[i].getName().substring(0,
								listFile[i].getName().indexOf("("));
						listFile[i].renameTo(new File(listFile[i].getParent()
								+ "\\" + newName));
						pCount++;
					}
					walkin(listFile[i]);
				} else { //if file not directory
					//                    if (listFile[i].getName().endsWith(pattern)) {
					//                        System.out.println(listFile[i].getPath());
					//                    }
				}
			}
		}
	}

	
	@SuppressWarnings("unused")
	private void preProcess() {

	}

	// Classes
	//TODO: Fix this to make it like the dynamic tree
	//TODO: Should not create a panel and such inside the method
	@SuppressWarnings("serial")
	private class RecursiveTree extends JPanel {
		public RecursiveTree(File dir) {
			JTree tree = new JTree(addNodes(null, dir));

			tree.addTreeSelectionListener(new TreeSelectionListener() {
				public void valueChanged(TreeSelectionEvent e) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) e
							.getPath().getLastPathComponent();
					folder = new File(e.getPath().getLastPathComponent()
							.toString());
					System.out.println("You selected " + node);
				}
			});

			JScrollPane scrollpane = new JScrollPane();
			scrollpane.getViewport().add(tree);
			add(scrollpane);
		}

		private DefaultMutableTreeNode addNodes(
				DefaultMutableTreeNode currentTop, File dir) {
			String currentPath = dir.getPath();

			DefaultMutableTreeNode currentDir = new DefaultMutableTreeNode(
					currentPath);
			if (currentTop != null) { // Should only be null at root
				currentTop.add(currentDir);
			}

			ArrayList<String> objectList = new ArrayList<String>();
			String[] dirList = dir.list();
			for (int i = 0; i < dirList.length; i++) {
				objectList.add(dirList[i]);
			}
			Collections.sort(objectList, String.CASE_INSENSITIVE_ORDER);

			File f;
			ArrayList<String> files = new ArrayList<String>();

			for (int i = 0; i < objectList.size(); i++) {
				String thisObject = (String) objectList.get(i);
				String newPath;
				if (currentPath.equals(".")) {
					newPath = thisObject;
				} else {
					newPath = currentPath + File.separator + thisObject;
				}

				if ((f = new File(newPath)).isDirectory()) {
					addNodes(currentDir, f);
				} else {
					files.add(thisObject);
				}
			}

			//			for (int fnum = 0; fnum < files.size(); fnum++) {
			//				currentDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));
			//			}

			return currentDir;
		}
	}

	@SuppressWarnings({ "serial", "unused" })
	private class DynamicTree extends DefaultMutableTreeNode {
		private boolean areChildrenDefined = false;
		File dir;

		public DynamicTree(File folder) {
			dir = folder;
		}

		public boolean isLeaf() {
			return (false);
		}

		public int getChildCount() {
			if (!areChildrenDefined)
				defineChildNodes();
			return (super.getChildCount());
		}

		private void defineChildNodes() {
			areChildrenDefined = true;
			File[] list = dir.listFiles();
			for (int i = 0; i < list.length; i++) {
				if (list[i].isDirectory()) {
					add(new DynamicTree(list[i]));
				}

			}
		}

		public String toString() {
			return dir.getName();
		}
	}

	// Main
	public static void main(String[] args) {
		new eBookRenamer();
	}
}
