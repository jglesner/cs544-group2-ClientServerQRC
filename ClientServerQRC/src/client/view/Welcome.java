
/**
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 *  This is the graphical user interface (GUI). User first run this class to 
 *  start a game.
 *  This application mainly apply javax.swing framework.
 *  
 */

package client.view;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
/**
 * Welcome is the first user interface.
 * 
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 */
public class Welcome extends javax.swing.JFrame {
    
	/** All swing modules are defined here*/
    private javax.swing.JButton jButton1; //"OK"
    private javax.swing.JButton jButton2; //"Quit"
    private javax.swing.JComboBox jComboBox1; //choose game box, right now contains
                                              //Texas Hold'em and Blackjack
    private javax.swing.JLabel jLabel1; //"welcome to poker online."
    private javax.swing.JLabel jLabel2; //Texas Hold'em picture
    private javax.swing.JLabel jLabel3;
    
    /**Initialize Welcome control */
    private boolean bGetGameList=false;
    private boolean quitGame=false;
    private String gameChoice=null;
    private String[] gamelist=null;
    private boolean bInit = false;
    
    /**
     * resetGameList - Reset choose game box
     * @param -1
     * 
     */
    public void resetGameList() {
    	if (bInit)
    	{
    		jComboBox1.setSelectedIndex(-1);
    		gameChoice=null;
    	}
    }
    
    /**
     * isbGetGameList - get game list
     * @return boolean
     */
    public boolean isbGetGameList() {
		return bGetGameList;
	}
    
    /**
     * setbGetGameList - set game list
     * @param bGetGameList
     */
	public void setbGetGameList(boolean bGetGameList) {
		this.bGetGameList = bGetGameList;
	}
    
	/**
	 * isQuitGame = get quit game value
	 * @return boolean
	 */
	public boolean isQuitGame() {
		return quitGame;
	}
    
	/**
	 * setQuitGame = set quit game value
	 * @param quitGame
	 */
	public void setQuitGame(boolean quitGame) {
		this.quitGame = quitGame;
	}

	/**
	 * getGameChoice - return game choice string
	 * @return string
	 */
	public String getGameChoice() {
		return gameChoice;
	}

	/**
	 * setGameChoice - set game choice string
	 * @param gameChoice
	 */
	public void setGameChoice(String gameChoice) {
		this.gameChoice = gameChoice;
	}

	/**
	 * getGamelist - get game list
	 * @return string array
	 */
	public String[] getGamelist() {
		return gamelist;
	}

	/**
	 * setGamelist - set game list
	 * @param gamelist
	 */
	public void setGamelist(String[] gamelist) {
		this.gamelist = gamelist;
	}

	
    /**
     * Creates new form welcome
     */
    public Welcome() {
    	bInit = false;
        update();
        bInit = true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * created under Netbeans JFrame
     */
    public void update() {

    	if (!bInit)
    	{
    		jLabel1 = new javax.swing.JLabel();
    		jLabel2 = new javax.swing.JLabel();
    		jButton1 = new javax.swing.JButton();
    		jButton2 = new javax.swing.JButton();
    		jLabel3 = new javax.swing.JLabel();
    		jComboBox1 = new javax.swing.JComboBox();
    	}

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PokerOnline");
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource("images/holdem.jpeg"))); 
        jLabel2.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        
        if(bGetGameList==false){
        	jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 18)); 
        	jLabel1.setText("Welcome to Poker Online! ");
        	jComboBox1.setVisible(false);
        } 
        else{
        	
        	jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(gamelist));
        	jComboBox1.setSelectedIndex(0);
        	jComboBox1.setVisible(true);
            jComboBox1.addActionListener(new java.awt.event.ActionListener() {
                 public void actionPerformed(java.awt.event.ActionEvent evt) {
                     JComboBox gamebox=(JComboBox)evt.getSource();
                     gameChoice=(String)gamebox.getSelectedItem();
                 }
             });
        }
       jButton1.setVisible(false);
       jButton2.setVisible(false);

       

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(0, 125, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 213, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 191, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(139, 139, 139))
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(jButton2))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1))
                    .add(layout.createSequentialGroup()
                        .add(156, 156, 156)
                        .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(192, 192, 192)
                        .add(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(51, 51, 51)
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jComboBox1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 73, Short.MAX_VALUE)
                .add(jButton2)
                .addContainerGap())
        );

        pack();
    }

    /**
     * popMessage - pop out welcome message
     * @param msg
     */
    public void popMessage(String msg){
	  JOptionPane.showMessageDialog(this, msg );
    }

}
