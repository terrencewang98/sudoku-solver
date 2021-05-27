import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener; 
import java.awt.event.ActionEvent;

public class Viewer{
	
	private BoardPanel boardPanel;

	public Viewer(){
		boardPanel = new BoardPanel();
		JPanel buttonPanel = createButtonPanel();
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(boardPanel, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.EAST);
		
		JFrame window = new JFrame("Sudoku Solver");
		window.add(mainPanel);
		window.pack();
        window.setVisible(true);
        window.setResizable(false);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	
		
	}

	private JPanel createButtonPanel(){
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JButton seqSolveButton = createSeqSolveButton();
		JButton parallelSolveButton = createParallelSolveButton();
		JButton resetButton = createResetButton();
		panel.add(seqSolveButton);
		panel.add(parallelSolveButton);
		panel.add(resetButton);

		return panel;
	}

	private JButton createSeqSolveButton(){
		JButton button = new JButton("Sequential Solve");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){ 
				boolean threading = false;
				boardPanel.solve(threading);
			}				
		});
		button.setMaximumSize(new Dimension(150, 30));
		button.setMinimumSize(new Dimension(150, 30));

		return button;
	}

	private JButton createParallelSolveButton(){
		JButton button = new JButton("Parallel Solve");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				boolean threading = true;
				boardPanel.solve(threading);
			}				
		});
		button.setMaximumSize(new Dimension(150, 30));
		button.setMinimumSize(new Dimension(150, 30));

		return button;
	}


	private JButton createResetButton(){
		JButton button = new JButton("Reset");
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){ 
				boardPanel.reset();
			}				
		});
		button.setMaximumSize(new Dimension(150, 30));
		button.setMinimumSize(new Dimension(150, 30));

		return button;
	}

	public static void main(String[] args){
		new Viewer();
	}
}