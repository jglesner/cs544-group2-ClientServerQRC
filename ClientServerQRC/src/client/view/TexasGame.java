/**
 *  @author GROUP 2, CS544-900-SPRING12, DREXEL UNIVERSITY
 *  Members: Jeremy Glesner, Dustin Overmiller, Yiqi Ju, Lei Yuan
 *  Project: Advanced Game Message Protocol Implementation
 *  
 *  This is the Texas Hold'em game GUI
 * 
 */
package client.view;

import client.card_game.ClientPokerModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import common.card_game.Card;


/**
 *  The TexasGame Class contains Texas Hold'em game GUI.
 *  
 */
public class TexasGame extends javax.swing.JFrame {
    
	  private javax.swing.JButton jButton1; //"Fold"
	    private javax.swing.JButton jButton2; //"Bet"
	    private javax.swing.JButton jButton3; //"Yes"
	    private javax.swing.JButton jButton4; //"Check"
	    private javax.swing.JLabel jLabel1; //flop 1
	    private javax.swing.JLabel jLabel2; //flop 2
	    private javax.swing.JLabel jLabel3; //flop 3
	    private javax.swing.JLabel jLabel4; //turn card
	    private javax.swing.JLabel jLabel5; //river card
	    private javax.swing.JLabel jLabel6; //"Texas Hold'em"
	    private javax.swing.JLabel jLabel7; //player card 1
	    private javax.swing.JLabel jLabel8; //player card 2
	    private javax.swing.JLabel jLabel9; //displayed message
	    private javax.swing.JLabel jLabel10; //"Ante"
	    private javax.swing.JLabel jLabel11; // "Bank Account"
	    private javax.swing.JLabel jLabel12; //"Pot"
	    private javax.swing.JLabel jLabel13; //pot amount
	    private javax.swing.JLabel jLabel14; //bank account
	    private javax.swing.JLabel jLabel16; //server card 2
	    private javax.swing.JLabel jLabel17; //server card 1
	    
	    /**Panel level*/
	    private javax.swing.JPanel jPanel1;
	    private javax.swing.JPanel jPanel2;
	    private javax.swing.JPanel jPanel3;
	    private javax.swing.JPanel jPanel4;
	    
	    private javax.swing.JTextField jTextField1; //player's ante enter field
   
        private ClientPokerModel pokerModel;
        private int userAnte; //player's enter ante
        private int minAnte; //minimum ante
        private String info; // information for the user
    
	    private InputState bPlayGames;
        private InputState follow; //player's enter ante
        
    /**
     * getFollow - return the current state
     * @return get current state
     */
    public int getFollow() {
        return follow.getState();
    }
    
    /**
     * setFollow - set the state
     * @param follow
     */
    public void setFollow(int follow) {
    	this.follow.setState(follow);
    }
    
    /**
     * getbPlayGames - get current state
     * @return current state
     */
    public int getbPlayGames() {
    	return bPlayGames.getState();
    }
    
    /**
     * setbPlayGames - set current state
     * @param state
     */
    public void setbPlayGames(int state) {
    	bPlayGames.setState(state);
    }

    /**
     * getInfo - get the string info
     * @return string info
     */
	public String getInfo() {
		return info;
	}

	/**
	 * setInfo - set the string info
	 * @param string info
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * call ClientPokerModel 
	 * @return pokerModel
	 */
	public ClientPokerModel getPokerModel() {
		return pokerModel;
	}

	/**
	 * setPokerModel - set the poker model
	 * @param pokerModel
	 */
	public void setPokerModel(ClientPokerModel pokerModel) {
		this.pokerModel = pokerModel;
	}

	/**
	 * getUserAnte - get the player's entered ante
	 * @return int userAnte
	 */
	public int getUserAnte() {
		return userAnte;
	}

	/**
	 * setUserAnte - set the player's ante
	 * @param userAnte
	 */
	public void setUserAnte(int userAnte) {
		this.userAnte = userAnte;
	}  
  
    /**
     * Creates new form TexasGame
     */
    public TexasGame( ClientPokerModel pokerModel) {
    	bPlayGames = new InputState(InputState.NOT_SET);
        follow = new InputState(InputState.NOT_SET);
        init( pokerModel);
    }
    
    /**
     *getMinAnte - get the minimum ante
     *@return int
     */
    public int getMinAnte() {
  		return minAnte;
  	}

    /**
     * setMinAnte - set the minimum ante
     * @param minAnte
     */
  	public void setMinAnte(int minAnte) {
  		this.minAnte = minAnte;
  	}
    
  	/**
  	 * init - initialize Texas Hold'em game with parameter from ClientPokerModel class
  	 * @param pokerModel
  	 */
    public void init(ClientPokerModel pokerModel){
    	   this.pokerModel=pokerModel;
           bPlayGames.setState(InputState.NOT_SET);
           follow.setState(InputState.NOT_SET);
    }
    
    /**
     * reset - reset pokerModel 
     * @param pokerModel
     */
    public void reset(ClientPokerModel pokerModel)
    {
    	this.pokerModel = pokerModel;
        bPlayGames.setState(InputState.NOT_SET);
        follow.setState(InputState.NOT_SET);
    	
    }
    
    /**
     * init - Texas Hold'em initialization method
     * created by Netbeans 
     */
    public void init() {

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("PokerOnline");
        setBackground(new java.awt.Color(0, 153, 153));

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));

        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setBackground(new java.awt.Color(102, 0, 0));
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);   
     
        jLabel12.setText("Pot:");

        jLabel13.setText(new Long(this.pokerModel.getlPotSize()).toString());
        jLabel13.setMaximumSize(new java.awt.Dimension(100000, 16));
        jLabel13.setPreferredSize(new java.awt.Dimension(100, 16));

        jPanel4.setBackground(new java.awt.Color(0, 153, 153));

        jLabel16.setBackground(new java.awt.Color(255, 255, 255));
        jLabel16.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel17.setBackground(new java.awt.Color(255, 255, 255));
        jLabel17.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(184, 184, 184)
                        .add(jLabel12)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(140, 140, 140)
                        .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(22, 22, 22)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(24, 24, 24)
                                .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(jLabel13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel6.setFont(new java.awt.Font("Rockwell Extra Bold", 0, 18)); // NOI18N
        jLabel6.setText("Texas Hold'em");

        jButton1.setText("Fold");
        jButton2.setText("Bet");
        jButton4.setText("Check");
        
        jButton1.setVisible(false);
        jButton2.setVisible(false);
        jButton4.setVisible(false);
        
        jPanel3.setBackground(new java.awt.Color(0, 153, 153));

        jLabel8.setBackground(new java.awt.Color(255, 255, 255));
        jLabel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel7.setBackground(new java.awt.Color(255, 255, 255));
        jLabel7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        
        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .add(12, 12, 12)
                .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 72, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel10.setText("Bet Amount:");

        jLabel11.setText("Bank Account:");

        jTextField1.setText(new Integer(this.minAnte).toString());
        jTextField1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextField1.setPreferredSize(new java.awt.Dimension(100, 28));
        jTextField1.setSize(new java.awt.Dimension(4, 0));


        jLabel14.setText(new Long(this.pokerModel.getlBankAmount()).toString());
        jLabel14.setMaximumSize(new java.awt.Dimension(100000, 16));
        jLabel14.setPreferredSize(new java.awt.Dimension(100, 16));

        jButton3.setText("Yes");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userAnte= Integer.parseInt(jTextField1.getText());
                bPlayGames.setState(InputState.FOLLOW);
            }
        });

     


        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(181, 181, 181)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel10)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(jTextField1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel11)
                                .add(18, 18, 18)
                                .add(jLabel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(0, 0, Short.MAX_VALUE))))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(41, 41, 41)
                                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(194, 194, 194)
                                .add(jLabel6))
                            .add(layout.createSequentialGroup()
                                .add(109, 109, 109)
                                .add(jButton4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(30, 30, 30)
                                .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(28, 28, 28)
                                .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(25, 25, 25)
                .add(jLabel6)
                .add(18, 18, 18)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton2)
                    .add(jButton4)
                    .add(jButton1))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(61, 61, 61)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jButton3)
                            .add(jTextField1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel10))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel11)
                            .add(jLabel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 22, Short.MAX_VALUE)
                        .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(45, 45, 45))
        );
        pack();
    }

  
    /**
     * getCardSource - show initialized icon of cards
     * @param args the command line arguments
     */
    public String getCardSource(Card c){
    	if(!c.isVisible())
    		return "images/card_back.png"; //show card back
    	int value=c.getCardValue();
    	String svalue="";
    	if(value<10)
    		svalue="0"+new Integer(value).toString();
    	else
    		svalue=new Integer(value).toString();
    		
    	String ssuit =new Integer(c.getCardSuite()).toString();
    	return "images/card_"+ssuit+svalue+".png";
    }
    public void setHoleCards(){
    	
        String sPlayCard1=this.getCardSource(this.pokerModel.getoPlayerCards()[0]);
        String sPlayCard2=this.getCardSource(this.pokerModel.getoPlayerCards()[1]);
        
        String sDealerCard1=this.getCardSource(this.pokerModel.getoDealerCards()[0]);
        String sDealerCard2=this.getCardSource(this.pokerModel.getoDealerCards()[1]);
        
        /**set player's cards */
		jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(sPlayCard1)));
		jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(sPlayCard2)));
		
		/**set server's cards */
		jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(sDealerCard1)));
		jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(sDealerCard2)));
				
		jButton1.setVisible(true);
		jButton2.setVisible(true);
		jButton4.setVisible(false);
        jTextField1.setEditable(false);
        jTextField1.setBorder(javax.swing.BorderFactory.createEmptyBorder());

        jButton3.setText("");
        jButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder());
        jButton3.setEnabled(false);
        basicRefresh();
        
        /**fold button action */
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               follow.setState(InputState.FOLD);
            }
        });
        
        /**bet button action */
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               follow.setState(InputState.FOLLOW);
            }
        });

		
    }
    
    /**setFlopCards - show flop cards */
    public void setFlopCards(){
        String sFlopCard1=this.getCardSource(this.pokerModel.getoFlopCards()[0]);
        String sFlopCard2=this.getCardSource(this.pokerModel.getoFlopCards()[1]);
        String sFlopCard3=this.getCardSource(this.pokerModel.getoFlopCards()[2]);
        
        /**set three flop cards icon */
        jLabel1.setIcon(new ImageIcon(getClass().getClassLoader().getResource(sFlopCard1)));
        jLabel2.setIcon(new ImageIcon(getClass().getClassLoader().getResource(sFlopCard2)));
        jLabel3.setIcon(new ImageIcon(getClass().getClassLoader().getResource(sFlopCard3)));
        
        /** only check button shows on this stage */
    	jButton4.setVisible(true);
    	
	    jButton4.addActionListener(new java.awt.event.ActionListener() {
	      public void actionPerformed(java.awt.event.ActionEvent evt) {
	         follow.setState(InputState.CHECK);
	      }
	     });
	      basicRefresh();
      
    }
    
    /** setTurnCard - set turn card */
    public void setTurnCard(){
    	  String sTurnCard1=this.getCardSource(this.pokerModel.getoTurnCard());
    	  jLabel4.setIcon(new ImageIcon(getClass().getClassLoader().getResource(sTurnCard1)));
    	  basicRefresh();
    }
    
    /** setAnte - set ante details */
    public void setAnte(){
    	  anteRefresh();
    }
    
    /** setRiverCard - set river card */
    public void setRiverCard(){
    	String sRiverCard1=this.getCardSource(this.pokerModel.getoRiverCard());
    	 String sDealerCard1=this.getCardSource(this.pokerModel.getoDealerCards()[0]);
         String sDealerCard2=this.getCardSource(this.pokerModel.getoDealerCards()[1]);
         
         /** set river card and show servers' cards */
    	jLabel5.setIcon(new ImageIcon(getClass().getClassLoader().getResource(sRiverCard1)));
		jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(sDealerCard1)));
		jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getClassLoader().getResource(sDealerCard2)));
		
		/** change the text of buttons */
		jButton1.setText("Go to Game List");
		jButton2.setText("Play Again");
		
		jButton1.setVisible(true);
		jButton2.setVisible(true);
		jButton4.setVisible(false);
		
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               follow.setState(InputState.FOLD);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               follow.setState(InputState.FOLLOW);
            }
        });

    	basicRefresh();
    }
    
    /** setFold - set the fold action */
    public void setFold(){

 		jButton4.setVisible(false);
 		jButton1.setText("Go to Game List");
		jButton2.setText("Play Again");
		
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               follow.setState(InputState.FOLD);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               follow.setState(InputState.FOLLOW);
            }
        });
        
		basicRefresh();
    }
    /** anteRefresh - update user info, bank amount and pot size*/
    public void anteRefresh() {
		jLabel9.setText(this.info);
	    jLabel14.setText(new Long(this.pokerModel.getlBankAmount()).toString());
        jLabel13.setText(new Long(this.pokerModel.getlPotSize()).toString());	    
    }
    
    /** basicRefresh - update bet amount, bank amount and pot size*/
    public void basicRefresh(){		
		jTextField1.setText(new Long(this.pokerModel.getlBetAmount()).toString());
		jLabel9.setText(this.info);
	    jLabel14.setText(new Long(this.pokerModel.getlBankAmount()).toString());
        jLabel13.setText(new Long(this.pokerModel.getlPotSize()).toString()+ "     Ante: "+new Integer(this.userAnte).toString());
	    
    }
    
    /**popMessage - pop out message */
    public void popMessage(String msg){
    	JOptionPane.showMessageDialog(this, msg );
    }
    
    /**InputState - handle state*/
    public class InputState {
    	
    	/**private variables */
    	private int state;
    	
    	/**public constants */
    	public static final int NOT_SET = 0;
    	public static final int FOLLOW = 1;
    	public static final int CHECK = 2;
    	public static final int FOLD = 3;
    	 
    	/**
    	 * Constructor
    	 * @param indicator
    	 */
		InputState(int indicator)
		{
			state = NOT_SET;
			if (indicator > 0 && indicator < 4)
			{
				state = indicator;
			}
		}
		
		/**
		 * getState - return the state
		 * @return int
		 */
		public int getState() {
			return state;
		}
		/**
		 * setState - set the state
		 * @param indicator
		 */
		public void setState(int indicator) {
			if (indicator >= 0 && indicator < 4)
			{
				this.state = indicator;
			}
		}
	}

}
    
 






