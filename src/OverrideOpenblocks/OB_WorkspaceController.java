package OverrideOpenblocks;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import Debug.ConsoleWindow;
import Language.*;

import edu.mit.blocks.codeblocks.BlockConnectorShape;
import edu.mit.blocks.codeblocks.BlockGenus;
import edu.mit.blocks.codeblocks.BlockLinkChecker;
import edu.mit.blocks.codeblocks.CommandRule;
import edu.mit.blocks.codeblocks.SocketRule;
import edu.mit.blocks.controller.WorkspaceController;
import edu.mit.blocks.workspace.SearchBar;
import edu.mit.blocks.workspace.SearchableContainer;
import edu.mit.blocks.workspace.Workspace;


public class OB_WorkspaceController extends WorkspaceController{
	
	private static OB_Workspace workspace = new OB_Workspace();
	
	private final int YES = 0;
	private final int NO = 1;
	private final int CANCEL = 2;
	
    private final static String DEFAULT_DRAWER_INFO = "resources/BlockDrawerList.xml";
    private final static String PROPERTY_PATH = "resources/startUp.properties";
    private static String resourcesFolderName = "resources";
    
    private static String blockDataPath;
    private String outputLanguagePath;
    private String outputDomain;
	
	private boolean isWorkspacePanelInitialized = false;
	
    //flag to indicate if a new lang definition file has been set
    private boolean langDefDirty = true;

    //flag to indicate if a workspace has been loaded/initialized
    private boolean workspaceLoaded = false;
    // last directory that was selected with open or save action
    private File lastDirectory;
    // file currently loaded in workspace
    private File selectedFile;
    // Reference kept to be able to update frame title with current loaded file
    private JFrame frame;
    
    //追加
    private JPanel ob_workspacePanel;
    //プロパティファイル
    private static LoadProperty lp = new LoadProperty(PROPERTY_PATH);
    
    
    @Override
    public OB_Workspace getWorkspace(){
    	return workspace;
    }
    

    
	public OB_WorkspaceController(){
		super(workspace);
	}
	
	
	
    public JComponent getButtonPanel() {
		
        JPanel buttonPanel = new JPanel();
        
        // Open
        OpenAction openAction = new OpenAction();
        buttonPanel.add(new JButton(openAction));
        // Save
        SaveAction saveAction = new SaveAction();
        buttonPanel.add(new JButton(saveAction));
        // Save as
        SaveAsAction saveAsAction = new SaveAsAction(saveAction);
        buttonPanel.add(new JButton(saveAsAction));
        
        //加筆
        //Output
        OutputAction outputAction = new OutputAction();
        buttonPanel.add(new JButton(outputAction));
        //DebugWindow
        DebugAction debugAction = new DebugAction();
        buttonPanel.add(new JButton(debugAction));
        
        
        return buttonPanel;
    }
	
	  /**
     * Action bound to "Open" action.
     */
    public class OpenAction extends AbstractAction {

        public static final long serialVersionUID = -2119679269613495704L;

        OpenAction() {
            super("Open");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser(lastDirectory);
            if (fileChooser.showOpenDialog((Component)e.getSource()) == JFileChooser.APPROVE_OPTION) {
                setSelectedFile(fileChooser.getSelectedFile());
                lastDirectory = selectedFile.getParentFile();
                String selectedPath = selectedFile.getPath();
                loadFreshWorkspace();
                System.out.println(selectedPath);
                loadProjectFromPath(selectedPath);
            }
        }
    }

    /**
     * Action bound to "Save" button.
     */
    public class SaveAction extends AbstractAction {
        public static final long serialVersionUID = -5540588250535739852L;
        SaveAction() {
            super("Save");
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            if (selectedFile == null) {
                JFileChooser fileChooser = new JFileChooser(lastDirectory);
                if (fileChooser.showSaveDialog((Component) evt.getSource()) == JFileChooser.APPROVE_OPTION) {
                    setSelectedFile(fileChooser.getSelectedFile());
                    lastDirectory = selectedFile.getParentFile();
                }
            }
            try {
                saveToFile(selectedFile);
            }
            catch (IOException e) {
                JOptionPane.showMessageDialog((Component) evt.getSource(),
                        e.getMessage());
            }
        }
    }

    /**
     * Action bound to "Save As..." button.
     */
    public class SaveAsAction extends AbstractAction {
         public static final long serialVersionUID = 3981294764824307472L;
        public final SaveAction saveAction;

        SaveAsAction(SaveAction saveAction) {
            super("Save As...");
            this.saveAction = saveAction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            selectedFile = null;
            // delegate to save action
            saveAction.actionPerformed(e);
        }
    }

    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
        System.out.println(selectedFile.getPath());
        frame.setTitle("BlockEducation - "+selectedFile.getPath());
    }

    /**
     * Loads all the block genuses, properties, and link rules of
     * a language specified in the pre-defined language def file.
     * @param root Loads the language specified in the Element root
     */
    @Override
    public void loadBlockLanguage(final Element root) {
        /* MUST load shapes before genuses in order to initialize
         connectors within each block correctly */
        BlockConnectorShape.loadBlockConnectorShapes(root);

        //load genuses
        BlockGenus.loadBlockGenera(workspace, root);

        //load rules
        BlockLinkChecker.addRule(workspace, new CommandRule(workspace));
        BlockLinkChecker.addRule(workspace, new SocketRule());

        //set the dirty flag for the language definition file
        //to false now that the lang file has been loaded
        langDefDirty = false;
    }
    
    /**
     * Saves the content of the workspace to the given file
     * @param file Destination file
     * @throws IOException If save failed
     */
    public void saveToFile(File file) throws IOException {
        FileWriter fileWriter = null;
        try {
           
        	//加筆
        	String fileName = file.getName().toString();
        	if(!fileName.substring(fileName.length()-4).equals(".xml")){
        		//ファイル名が.xmlではない場合は.xmlとして保存する
	        	File renameFile = new File(file.getPath()+fileName+".xml");
	        	file = renameFile;
        	}
        	//加筆ここまで
        	
            fileWriter = new FileWriter(file);       
            fileWriter.write(getSaveString());
        }
        finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }
    
//    /**
//     * This method creates and lays out the entire workspace panel with its
//     * different components.  Workspace and language data not loaded in
//     * this function.
//     * Should be call only once at application startup.
//     */
//    private void initWorkspacePanel() {
//        ob_workspacePanel = new JPanel();
//        ob_workspacePanel.setLayout(new BorderLayout());
//        ob_workspacePanel.add(workspace, BorderLayout.CENTER);
//        isWorkspacePanelInitialized = true;
//    }
//    
//    /**
//     * Returns the JComponent of the entire workspace.
//     * @return the JComponent of the entire workspace.
//     */
//    @Override
//    public JComponent getWorkspacePanel() {
//        if (!isWorkspacePanelInitialized) {
//            initWorkspacePanel();
//        }
//        return ob_workspacePanel;
//    }
    
    /**
     * Create the GUI and show it.  For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    @Override
    protected void createAndShowGUI() {
        frame = new JFrame("BlockEducation Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBounds(100, 100, 700, 500);
        frame.add(topPane(), BorderLayout.PAGE_START);
        frame.add(getWorkspacePanel(), BorderLayout.CENTER);
        frame.add(getButtonPanel(), BorderLayout.SOUTH);
        frame.setVisible(true);
    }
    
    protected JComponent topPane(){
        final SearchBar sb = new SearchBar("Search blocks",
                "Search for blocks in the drawers and workspace", workspace);
        for (final SearchableContainer con : getAllSearchableContainers()) {
            sb.addSearchableContainer(con);
        }
        final JPanel topPane = new JPanel();
        sb.getComponent().setPreferredSize(new Dimension(130, 23));
        
        //コンボボックス作成のためのファイル一覧読み込み
        File file = new File(resourcesFolderName);
        File[] files = file.listFiles();
        String[] fileNames = new String[files.length];

        //開いているファイルを予め選択しておく
        int selectIndex = 0;
        String[] path = blockDataPath.split("/");
        String selectFileName = path[path.length-1];

        //ファイルパスからファイル名生成
        int i=0;
        for(File f: files){
        	fileNames[i] =f.getName();
        	if(fileNames[i].equals(selectFileName)){
        		selectIndex=i;
        	}
        	i++;
        }
        String[] showNames = removeExtends(fileNames);
        final JComboBox cb = new JComboBox(showNames);
        cb.setSelectedIndex(selectIndex);
        
        cb.addActionListener(
        		new ActionListener(){
        		      public void actionPerformed(ActionEvent e){
        		    	  
        		    	  //セーブするか聞く
        		    	  int option = JOptionPane.showConfirmDialog(frame, "保存しますか？", 
        		    			  "保存確認", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
        		    	  switch(option){
        		    	  case YES:
        		    		  	SaveAsAction saa = new SaveAsAction(new SaveAction());
        		    		  	saa.actionPerformed(e);
        		    		  	break;
        		    		  	
        		    	  case NO:
        		    		  	break;
        		    
        		    	  case CANCEL:
        		    		  	return;
        		    	  }
        		    	  
        		    	  String selectedFileName = resourcesFolderName + "/" + cb.getSelectedItem().toString() + ".xml";
        		    	  //新しいwindowを開く
        		    	  newWindow(selectedFileName);
        		      }
        		});
        
        topPane.add(cb);
        topPane.add(sb.getComponent());
        return topPane;
    }
    
    @Override
    public void loadFreshWorkspace() {
        if (workspaceLoaded) {
            resetWorkspace();
        }
        if (langDefDirty) {
            loadBlockLanguage(langDefRoot);
        }
        
        if(blockDrawerDirty){
        	setBlockDrawerRoot(blockDataPath);
        }
        
        //出力言語の設定を抜き出す
        setOutputLanguage(blockDrawerRoot);
        
        workspace.loadWorkspaceFrom(null, langDefRoot, blockDrawerRoot);
        workspaceLoaded = true;
    }
    
    private void setOutputLanguage(Element blockDrawerRoot){
    	NodeList outputs = blockDrawerRoot.getElementsByTagName("Output");
    	
    	for(int j=0; j<outputs.getLength(); j++){
	    	Node output = outputs.item(j);
	    	
	    	if(output.getNodeName().equals("Output")){
		    	NodeList outputChildren = output.getChildNodes();
		    	
		    	for(int i=0; i<outputChildren.getLength(); i++){
		    		Node properties = outputChildren.item(i);

			    	if(properties.getNodeName().equals("FilePath")){
			    		this.outputLanguagePath = properties.getTextContent();
			    		System.out.println(this.outputLanguagePath);
			    	}
			    	
			    	if(properties.getNodeName().equals("FileExtends")){
			    		this.outputDomain = properties.getTextContent();
			    	}
		    	}
	    	}
    	}
    }
    
    /**
     * Sets the file path for the language definition file, if the
     * language definition file is located in
     */
    public void setBlockDrawerRoot(final String filePath) {
        InputStream in = null;
        try {
            in = new FileInputStream(filePath);
            setBlockDrawerRoot(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    private Element blockDrawerRoot;
    private boolean blockDrawerDirty = true;
 
    private void setBlockDrawerRoot(InputStream in){
    	final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder;
        final Document doc;
        try {
            builder = factory.newDocumentBuilder();
            doc = builder.parse(in);
            blockDrawerRoot = doc.getDocumentElement();
            blockDrawerDirty = false;
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void main(final String[] args) {
//        if (args.length < 1) {
//            System.err.println("usage: WorkspaceController lang_def.xml");
//            System.exit(1);
//        }
    	newWindow();
    }
    
    private static void newWindow(final String filePath){
    	workspace.reset();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {  
		    	blockDataPath = filePath;
		    	OB_WorkspaceController wc = new OB_WorkspaceController();
		        wc.setLangDefFilePath(lp.getBlockAllDataAddress());
		        wc.loadFreshWorkspace();
		        wc.getWorkspace().loadBlockEducationModule();
            }
        });
    }

    private static void newWindow(){
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override public void run() {            	
//                try {
// 					wait();
// 				} catch (InterruptedException e) {
// 					// TODO Auto-generated catch block
// 					e.printStackTrace();
// 					System.exit(1);
// 				}
                
            	OB_WorkspaceController wc = new OB_WorkspaceController();
            	resourcesFolderName = lp.getResourcesFolderPath();
                             
                if(lp.isSelected()){
                	blockDataPath = selectFile();
                }
                else{
                	blockDataPath = lp.getBlockDrawerListAddress();
                }
//                wc.setLangDefFilePath(args[0]);
 
                wc.setLangDefFilePath(lp.getBlockAllDataAddress());
                wc.loadFreshWorkspace();
                wc.createAndShowGUI();
                wc.getWorkspace().loadBlockEducationModule();
            }
        });
    }
    
    private String[] removeExtends(String[] names){
    	int i=0;
    	String[] result = new String[names.length];
    	for(String name:names){
    		String[] str = name.split("\\.");
    		if(str.length > 0){
    			result[i++] = str[0];
    		}
    	}
    	return result;
    }
    
    protected static String selectFile(){
        File file = new File(resourcesFolderName);
        File[] files = file.listFiles();
        
        String[] fileName = new String[files.length+1];
        fileName[0] = "Default";
        
        for(int i=0; i<files.length; i++){
        	fileName[i+1] = files[i].getName();
        }
        
        ImageIcon icon = null;
        JFrame dummy = new JFrame();
        Object value = JOptionPane.showInputDialog(dummy, "ファイル選択", 
        	      "どのファイルを読み込みますか？", JOptionPane.PLAIN_MESSAGE,
        	      icon, fileName, fileName[0]);
        
        if(value == null || value.equals("Default")){
        	return DEFAULT_DRAWER_INFO;
        }
        else{
        	return resourcesFolderName + "/" +(String)value;
        }
    }
    
    /**
     * Action bound to "Output" action.
     */
    private class OutputAction extends AbstractAction {
    	
		private static final long serialVersionUID = 1L;
		
		
		OutputAction() {
			super("ソースコード出力");
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	   
//	    	debug();
	    	String code = getSaveString();
//	    	System.err.println("debug::"+code+"debugEND");
	    	
	    	try{
		    	DocumentBuilderFactory dbfactory = DocumentBuilderFactory.newInstance();
		    	DocumentBuilder docbuilder = dbfactory.newDocumentBuilder();
		    	Document doc = docbuilder.parse(new ByteArrayInputStream(code.getBytes("UTF-8")));
		    	
		        OutputCode outputCode = new OutputCode(doc);
		        ToCode toCode = new ToCode(outputCode.getBrockStringArray(outputLanguagePath), outputCode.getBlockList());
		        CodeWritter codeWritter = new CodeWritter(toCode.connectionAllBlockCode());
		        codeWritter.writting("testfile", outputDomain);
		        
		        //debugできあがったコードを吐く
		        System.out.println(codeWritter.getCode());
		        
	    	}catch(Exception err){
	    		err.printStackTrace();
	    	}
	    }
    }
    
    /**
     * Action bound to "Output" action.
     */
    private class DebugAction extends AbstractAction {
    	
		private static final long serialVersionUID = 1L;
		
		private ConsoleWindow cWindow;
		
		DebugAction() {
			super("デバッグ");
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	    	if(cWindow == null){
	    		cWindow = new ConsoleWindow(workspace);
	    	}
	    	else{
	    		cWindow.reload(workspace);
	    	}
	    }
    }


	
}

