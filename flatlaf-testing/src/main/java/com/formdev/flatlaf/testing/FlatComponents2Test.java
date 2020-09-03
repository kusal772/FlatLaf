/*
 * Copyright 2019 FormDev Software GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.formdev.flatlaf.testing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import com.formdev.flatlaf.icons.FlatMenuArrowIcon;
import net.miginfocom.swing.*;

/**
 * @author Karl Tauber
 */
public class FlatComponents2Test
	extends FlatTestPanel
{
	public static void main( String[] args ) {
		SwingUtilities.invokeLater( () -> {
			FlatTestFrame frame = FlatTestFrame.create( args, "FlatComponents2Test" );
			frame.showFrame( FlatComponents2Test::new );
		} );
	}

	private final TestListModel listModel;
	private final TestTreeModel treeModel;
	private final TestTableModel tableModel;

	FlatComponents2Test() {
		initComponents();

		// list model
		listModel = new TestListModel( (Integer) listRowCountSpinner.getValue() );
		list1.setModel( listModel );
		list2.setModel( listModel );

		// tree model
		treeModel = new TestTreeModel( (Integer) treeRowCountSpinner.getValue() );
		tree1.setModel( treeModel );
		tree2.setModel( treeModel );

		// table model
		tableModel = new TestTableModel( (Integer) tableRowCountSpinner.getValue() );
		table1.setModel( tableModel );

		// table column editors
		TableColumnModel cm = table1.getColumnModel();
		String[] months = new String[] {
			"January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December"
		};
		cm.getColumn(2).setCellEditor( new DefaultCellEditor( new JComboBox<>( months ) ) );
		JComboBox<String> editableComboBox = new JComboBox<>( months );
		editableComboBox.setEditable( true );
		cm.getColumn(3).setCellEditor( new DefaultCellEditor( editableComboBox ) );

		expandTree( tree1 );
		expandTree( tree2 );
	}

	private void expandTree( JTree tree ) {
		int count = tree.getRowCount();
		for( int i = count - 1; i >= 0; i-- )
			tree.expandRow( i );
	}

	private void listRowCountChanged() {
		listModel.setSize( (Integer) listRowCountSpinner.getValue() );
	}

	private void treeRowCountChanged() {
		int rowCount = (Integer) treeRowCountSpinner.getValue();

		// round to 20
		if( rowCount % 20 != 0 ) {
			rowCount += 20 - (rowCount % 20);
			treeRowCountSpinner.setValue( rowCount );
		}

		int oldCount1 = tree1.getRowCount();
		int oldCount2 = tree2.getRowCount();

		treeModel.setRowCount( rowCount );

		int newCount1 = tree1.getRowCount();
		int newCount2 = tree2.getRowCount();

		// expand added rows
		for( int i = newCount1 - 1; i >= oldCount1; i-- )
			tree1.expandRow( i );
		for( int i = newCount2 - 1; i >= oldCount2; i-- )
			tree2.expandRow( i );
	}

	private void tableRowCountChanged() {
		tableModel.setRowCount( (Integer) tableRowCountSpinner.getValue() );
	}

	private void dndChanged() {
		boolean dnd = dndCheckBox.isSelected();
		list1.setDragEnabled( dnd );
		list2.setDragEnabled( dnd );
		tree1.setDragEnabled( dnd );
		tree2.setDragEnabled( dnd );
		table1.setDragEnabled( dnd );

		DropMode dropMode = dnd ? DropMode.ON_OR_INSERT : DropMode.USE_SELECTION;
		list1.setDropMode( dropMode );
		tree1.setDropMode( dropMode );
		table1.setDropMode( dropMode );

		String key = "FlatLaf.oldTransferHandler";
		if( dnd ) {
			list1.putClientProperty( key, list1.getTransferHandler() );
			list1.setTransferHandler( new DummyTransferHandler() );

			tree1.putClientProperty( key, tree1.getTransferHandler() );
			tree1.setTransferHandler( new DummyTransferHandler() );

			table1.putClientProperty( key, table1.getTransferHandler() );
			table1.setTransferHandler( new DummyTransferHandler() );
		} else {
			list1.setTransferHandler( (TransferHandler) list1.getClientProperty( key ) );
			tree1.setTransferHandler( (TransferHandler) tree1.getClientProperty( key ) );
			table1.setTransferHandler( (TransferHandler) table1.getClientProperty( key ) );
		}
	}

	private void tableHeaderButtonChanged() {
		boolean show = tableHeaderButtonCheckBox.isSelected();
		JButton button = null;
		if( show ) {
			button = new JButton( new FlatMenuArrowIcon() );
			button.addActionListener( e -> {
				JOptionPane.showMessageDialog( this, "hello" );
			} );
		}
		scrollPane5.setCorner( JScrollPane.UPPER_TRAILING_CORNER, button );
	}

	private void rowSelectionChanged() {
		table1.setRowSelectionAllowed( rowSelectionCheckBox.isSelected() );
	}

	private void columnSelectionChanged() {
		table1.setColumnSelectionAllowed( columnSelectionCheckBox.isSelected() );
	}

	private void showHorizontalLinesChanged() {
		table1.setShowHorizontalLines( showHorizontalLinesCheckBox.isSelected() );
	}

	private void showVerticalLinesChanged() {
		table1.setShowVerticalLines( showVerticalLinesCheckBox.isSelected() );
	}

	private void intercellSpacingChanged() {
		table1.setIntercellSpacing( intercellSpacingCheckBox.isSelected() ? new Dimension( 1, 1 ) : new Dimension() );
	}

	private void redGridColorChanged() {
		table1.setGridColor( redGridColorCheckBox.isSelected() ? Color.red : UIManager.getColor( "Table.gridColor" ) );
	}

	@Override
	public void updateUI() {
		super.updateUI();

		EventQueue.invokeLater( () -> {
			showHorizontalLinesChanged();
			showVerticalLinesChanged();
			intercellSpacingChanged();
		} );
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		JLabel textFieldLabel = new JLabel();
		JTextField textField1 = new JTextField();
		JTextField textField2 = new JTextField();
		JLabel listLabel = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		list1 = new JList<>();
		JScrollPane scrollPane2 = new JScrollPane();
		list2 = new JList<>();
		JPanel listOptionsPanel = new JPanel();
		JLabel listRowCountLabel = new JLabel();
		listRowCountSpinner = new JSpinner();
		JLabel treeLabel = new JLabel();
		JScrollPane scrollPane3 = new JScrollPane();
		tree1 = new JTree();
		JScrollPane scrollPane4 = new JScrollPane();
		tree2 = new JTree();
		JPanel treeOptionsPanel = new JPanel();
		JLabel treeRowCountLabel = new JLabel();
		treeRowCountSpinner = new JSpinner();
		JLabel tableLabel = new JLabel();
		scrollPane5 = new JScrollPane();
		table1 = new JTable();
		JPanel tableOptionsPanel = new JPanel();
		JLabel tableRowCountLabel = new JLabel();
		tableRowCountSpinner = new JSpinner();
		showHorizontalLinesCheckBox = new JCheckBox();
		showVerticalLinesCheckBox = new JCheckBox();
		intercellSpacingCheckBox = new JCheckBox();
		redGridColorCheckBox = new JCheckBox();
		rowSelectionCheckBox = new JCheckBox();
		columnSelectionCheckBox = new JCheckBox();
		dndCheckBox = new JCheckBox();
		tableHeaderButtonCheckBox = new JCheckBox();

		//======== this ========
		setLayout(new MigLayout(
			"ltr,insets dialog,hidemode 3",
			// columns
			"[]" +
			"[200,fill]" +
			"[200,fill]" +
			"[fill]",
			// rows
			"[]" +
			"[150,grow,sizegroup 1,fill]" +
			"[150,grow,sizegroup 1,fill]" +
			"[150,grow,sizegroup 1,fill]"));

		//---- textFieldLabel ----
		textFieldLabel.setText("JTextField:");
		add(textFieldLabel, "cell 0 0");

		//---- textField1 ----
		textField1.setText("editable");
		add(textField1, "cell 1 0,growx");

		//---- textField2 ----
		textField2.setText("disabled");
		textField2.setEnabled(false);
		add(textField2, "cell 2 0,growx");

		//---- listLabel ----
		listLabel.setText("JList:");
		add(listLabel, "cell 0 1,aligny top,growy 0");

		//======== scrollPane1 ========
		{
			scrollPane1.setViewportView(list1);
		}
		add(scrollPane1, "cell 1 1");

		//======== scrollPane2 ========
		{

			//---- list2 ----
			list2.setEnabled(false);
			scrollPane2.setViewportView(list2);
		}
		add(scrollPane2, "cell 2 1");

		//======== listOptionsPanel ========
		{
			listOptionsPanel.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[90,fill]",
				// rows
				"[]"));

			//---- listRowCountLabel ----
			listRowCountLabel.setText("Row count:");
			listOptionsPanel.add(listRowCountLabel, "cell 0 0");

			//---- listRowCountSpinner ----
			listRowCountSpinner.setModel(new SpinnerNumberModel(20, 0, null, 10));
			listRowCountSpinner.addChangeListener(e -> listRowCountChanged());
			listOptionsPanel.add(listRowCountSpinner, "cell 1 0");
		}
		add(listOptionsPanel, "cell 3 1");

		//---- treeLabel ----
		treeLabel.setText("JTree:");
		add(treeLabel, "cell 0 2,aligny top,growy 0");

		//======== scrollPane3 ========
		{

			//---- tree1 ----
			tree1.setShowsRootHandles(true);
			tree1.setEditable(true);
			scrollPane3.setViewportView(tree1);
		}
		add(scrollPane3, "cell 1 2");

		//======== scrollPane4 ========
		{

			//---- tree2 ----
			tree2.setEnabled(false);
			scrollPane4.setViewportView(tree2);
		}
		add(scrollPane4, "cell 2 2");

		//======== treeOptionsPanel ========
		{
			treeOptionsPanel.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[fill]" +
				"[90,fill]",
				// rows
				"[]"));

			//---- treeRowCountLabel ----
			treeRowCountLabel.setText("Row count:");
			treeOptionsPanel.add(treeRowCountLabel, "cell 0 0");

			//---- treeRowCountSpinner ----
			treeRowCountSpinner.setModel(new SpinnerNumberModel(20, 20, null, 20));
			treeRowCountSpinner.addChangeListener(e -> treeRowCountChanged());
			treeOptionsPanel.add(treeRowCountSpinner, "cell 1 0");
		}
		add(treeOptionsPanel, "cell 3 2");

		//---- tableLabel ----
		tableLabel.setText("JTable:");
		add(tableLabel, "cell 0 3,aligny top,growy 0");

		//======== scrollPane5 ========
		{

			//---- table1 ----
			table1.setAutoCreateRowSorter(true);
			scrollPane5.setViewportView(table1);
		}
		add(scrollPane5, "cell 1 3 2 1,width 300");

		//======== tableOptionsPanel ========
		{
			tableOptionsPanel.setLayout(new MigLayout(
				"hidemode 3",
				// columns
				"[]" +
				"[90,fill]",
				// rows
				"[]" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]0" +
				"[]"));

			//---- tableRowCountLabel ----
			tableRowCountLabel.setText("Row count:");
			tableOptionsPanel.add(tableRowCountLabel, "cell 0 0");

			//---- tableRowCountSpinner ----
			tableRowCountSpinner.setModel(new SpinnerNumberModel(20, 0, null, 10));
			tableRowCountSpinner.addChangeListener(e -> tableRowCountChanged());
			tableOptionsPanel.add(tableRowCountSpinner, "cell 1 0");

			//---- showHorizontalLinesCheckBox ----
			showHorizontalLinesCheckBox.setText("show horizontal lines");
			showHorizontalLinesCheckBox.addActionListener(e -> showHorizontalLinesChanged());
			tableOptionsPanel.add(showHorizontalLinesCheckBox, "cell 0 1 2 1");

			//---- showVerticalLinesCheckBox ----
			showVerticalLinesCheckBox.setText("show vertical lines");
			showVerticalLinesCheckBox.addActionListener(e -> showVerticalLinesChanged());
			tableOptionsPanel.add(showVerticalLinesCheckBox, "cell 0 2 2 1");

			//---- intercellSpacingCheckBox ----
			intercellSpacingCheckBox.setText("intercell spacing");
			intercellSpacingCheckBox.addActionListener(e -> intercellSpacingChanged());
			tableOptionsPanel.add(intercellSpacingCheckBox, "cell 0 3 2 1");

			//---- redGridColorCheckBox ----
			redGridColorCheckBox.setText("red grid color");
			redGridColorCheckBox.addActionListener(e -> redGridColorChanged());
			tableOptionsPanel.add(redGridColorCheckBox, "cell 0 4 2 1");

			//---- rowSelectionCheckBox ----
			rowSelectionCheckBox.setText("row selection");
			rowSelectionCheckBox.setSelected(true);
			rowSelectionCheckBox.addActionListener(e -> rowSelectionChanged());
			tableOptionsPanel.add(rowSelectionCheckBox, "cell 0 5 2 1");

			//---- columnSelectionCheckBox ----
			columnSelectionCheckBox.setText("column selection");
			columnSelectionCheckBox.addActionListener(e -> columnSelectionChanged());
			tableOptionsPanel.add(columnSelectionCheckBox, "cell 0 6 2 1");

			//---- dndCheckBox ----
			dndCheckBox.setText("enable drag and drop");
			dndCheckBox.setMnemonic('D');
			dndCheckBox.addActionListener(e -> dndChanged());
			tableOptionsPanel.add(dndCheckBox, "cell 0 7 2 1");

			//---- tableHeaderButtonCheckBox ----
			tableHeaderButtonCheckBox.setText("show button in table header");
			tableHeaderButtonCheckBox.addActionListener(e -> tableHeaderButtonChanged());
			tableOptionsPanel.add(tableHeaderButtonCheckBox, "cell 0 8 2 1");
		}
		add(tableOptionsPanel, "cell 3 3");
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JList<String> list1;
	private JList<String> list2;
	private JSpinner listRowCountSpinner;
	private JTree tree1;
	private JTree tree2;
	private JSpinner treeRowCountSpinner;
	private JScrollPane scrollPane5;
	private JTable table1;
	private JSpinner tableRowCountSpinner;
	private JCheckBox showHorizontalLinesCheckBox;
	private JCheckBox showVerticalLinesCheckBox;
	private JCheckBox intercellSpacingCheckBox;
	private JCheckBox redGridColorCheckBox;
	private JCheckBox rowSelectionCheckBox;
	private JCheckBox columnSelectionCheckBox;
	private JCheckBox dndCheckBox;
	private JCheckBox tableHeaderButtonCheckBox;
	// JFormDesigner - End of variables declaration  //GEN-END:variables

	private final String[] randomRowStrings = new String[1000];
	private final Random random = new Random();

	private String randomRowString( int row ) {
		int index = row % randomRowStrings.length;

		String s = randomRowStrings[index];
		if( s != null )
			return s;

		char[] chars = new char[3 + random.nextInt( 15 - 3 )];
		for( int i = 0; i < chars.length; i++ )
			chars[i] = (char) ((i == 0 ? 'A' : 'a') + random.nextInt( 26 ));
		if( chars.length > 6 )
			chars[chars.length / 2] = ' ';
		s = new String( chars );

		randomRowStrings[index] = s;
		return s;
	}

	//---- class DummyTransferHandler -----------------------------------------

	private static class DummyTransferHandler
		extends TransferHandler
	{
		@Override
		protected Transferable createTransferable( JComponent c ) {
			if( c instanceof JList && ((JList<?>)c).isSelectionEmpty() )
				return null;
			if( c instanceof JTree && ((JTree)c).isSelectionEmpty() )
				return null;
			if( c instanceof JTable && ((JTable)c).getSelectionModel().isSelectionEmpty() )
				return null;

			return new StringSelection( "dummy" );
		}

		@Override
		public int getSourceActions( JComponent c ) {
			return COPY;
		}

		@Override
		public boolean canImport( TransferSupport support ) {
			return support.isDataFlavorSupported( DataFlavor.stringFlavor );
		}

		@Override
		public boolean importData( TransferSupport support ) {
			System.out.println( support.getDropLocation() );
			return false;
		}
	}

	//---- class TestListModel ------------------------------------------------

	private class TestListModel
		extends AbstractListModel<String>
	{
		private int size;

		TestListModel( int size ) {
			setSize( size );
		}

		void setSize( int size ) {
			int oldSize = this.size;
			this.size = size;

			// fire event
			if( size > oldSize )
				fireIntervalAdded( this, oldSize, size - 1 );
			else if( size < oldSize )
				fireIntervalRemoved( this, size, oldSize - 1 );
		}

		@Override
		public int getSize() {
			return size;
		}

		@Override
		public String getElementAt( int index ) {
			return (index < 20)
				? "item " + (index + 1)
				: "item " + (index + 1) + "   " + randomRowString( index );
		}
	}

	//---- TestTreeModel ------------------------------------------------------

	private class TestTreeModel
		extends DefaultTreeModel
	{
		private int rowCount;

		TestTreeModel( int rowCount ) {
			super( new DefaultMutableTreeNode( "JTree" ) );
			setRowCount( rowCount );
		}

		void setRowCount( int rowCount ) {
			if( rowCount < 20 )
				rowCount = 20;

			if( rowCount > this.rowCount ) {
				// add nodes
				int oldRootChildCount = root.getChildCount();
				while( rowCount > this.rowCount ) {
					addTwentyNodes( (DefaultMutableTreeNode) root, this.rowCount );
					this.rowCount += 20;
				}

				// fire event
				int newRootChildCount = root.getChildCount();
				if( newRootChildCount > oldRootChildCount ) {
					int[] childIndices = new int[newRootChildCount - oldRootChildCount];
					for( int i = 0; i < childIndices.length; i++ )
						childIndices[i] = oldRootChildCount + i;
					nodesWereInserted( root, childIndices );
				}
			} else if( rowCount < this.rowCount ) {
				// remove nodes
				int oldRootChildCount = root.getChildCount();
				List<Object> removedChildren = new ArrayList<>();
				while( this.rowCount > rowCount ) {
					int index = root.getChildCount() - 1;
					removedChildren.add( 0, root.getChildAt( index ) );
					((DefaultMutableTreeNode)root).remove( index );
					this.rowCount -= 20;
				}

				// fire event
				int newRootChildCount = root.getChildCount();
				if( newRootChildCount < oldRootChildCount ) {
					int[] childIndices = new int[oldRootChildCount - newRootChildCount];
					for( int i = 0; i < childIndices.length; i++ )
						childIndices[i] = newRootChildCount + i;
					nodesWereRemoved( root, childIndices, removedChildren.toArray() );
				}
			}
		}

		private void addTwentyNodes( DefaultMutableTreeNode root, int firstRowIndex ) {
			if( firstRowIndex == 0 ) {
				DefaultMutableTreeNode n = new DefaultMutableTreeNode( "colors" );
				n.add( new DefaultMutableTreeNode( "blue" ) );
				n.add( new DefaultMutableTreeNode( "violet" ) );
				n.add( new DefaultMutableTreeNode( "red" ) );
				n.add( new DefaultMutableTreeNode( "yellow" ) );
				root.add( n );

				n = new DefaultMutableTreeNode( "sports" );
				n.add( new DefaultMutableTreeNode( "basketball" ) );
				n.add( new DefaultMutableTreeNode( "soccer" ) );
				n.add( new DefaultMutableTreeNode( "football" ) );
				n.add( new DefaultMutableTreeNode( "hockey" ) );
				root.add( n );

				n = new DefaultMutableTreeNode( "food" );
				n.add( new DefaultMutableTreeNode( "hot dogs" ) );

				DefaultMutableTreeNode n2 = new DefaultMutableTreeNode( "pizza" );
				n2.add( new DefaultMutableTreeNode( "pizza aglio e olio" ) );
				n2.add( new DefaultMutableTreeNode( "pizza calabrese" ) );
				n2.add( new DefaultMutableTreeNode( "pizza infernale" ) );
				n2.add( new DefaultMutableTreeNode( "pizza margherita bianca" ) );
				n2.add( new DefaultMutableTreeNode( "pizza quattro stagioni" ) );
				n.add( n2 );

				n.add( new DefaultMutableTreeNode( "ravioli" ) );
				n.add( new DefaultMutableTreeNode( "bananas" ) );
				root.add( n );
			} else {
				DefaultMutableTreeNode n = new DefaultMutableTreeNode( "item " + firstRowIndex + "   " + randomRowString( firstRowIndex ) );
				for( int i = 1; i < 20; i++ ) {
					int index = firstRowIndex + i;
					n.add( new DefaultMutableTreeNode( "item " + index + "   " + randomRowString( index ) ) );
				}
				root.add( n );
			}
		}
	}

	//---- TestTableModel -----------------------------------------------------

	private class TestTableModel
		extends AbstractTableModel
	{
		private final String[] columnNames = new String[] {
			"Not editable", "Text", "Combo", "Combo Editable", "Integer", "Boolean"
		};

		private final Class<?>[] columnTypes = new Class<?>[] {
			Object.class, Object.class, String.class, String.class, Integer.class, Boolean.class
		};

		private final boolean[] columnEditable = new boolean[] {
			false, true, true, true, true, true
		};

		private final Object[][] rows = new Object[][] {
			{ "item 1", "item 1b", "January", "July", 123, null },
			{ "item 2", "item 2b", "February", "August", 456, true },
			{ "item 3", null, "March", null, null, null },
			{ "item 4", null, "April", null, null, null },
			{ "item 5", null, "May", null, null, null },
			{ "item 6", null, "June", null, null, null },
			{ "item 7", null, "July", null, null, null },
			{ "item 8", null, "August", null, null, null },
			{ "item 9", null, "September", null, null, null },
			{ "item 10", null, "October", null, null, null },
			{ "item 11", null, "November", null, null, null },
			{ "item 12", null, "December", null, null, null },
		};

		private int rowCount = rows.length;
		private final Map<Integer, Object[]> moreRowsMap = new HashMap<>();

		TestTableModel( int rowCount ) {
			setRowCount( rowCount );
		}

		void setRowCount( int rowCount ) {
			int oldRowCount = this.rowCount;
			this.rowCount = rowCount;

			// fire event
			if( rowCount > oldRowCount )
				fireTableRowsInserted( oldRowCount, rowCount - 1 );
			else if( rowCount < oldRowCount )
				fireTableRowsDeleted( rowCount, oldRowCount - 1 );
		}

		@Override
		public int getRowCount() {
			return rowCount;
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public String getColumnName( int columnIndex ) {
			return columnNames[columnIndex];
		}

		@Override
		public Class<?> getColumnClass( int columnIndex ) {
			return columnTypes[columnIndex];
		}

		@Override
		public boolean isCellEditable( int rowIndex, int columnIndex ) {
			return columnEditable[columnIndex];
		}

		@Override
		public Object getValueAt( int rowIndex, int columnIndex ) {
			if( rowIndex < rows.length )
				return rows[rowIndex][columnIndex];

			Object[] row = moreRowsMap.get( rowIndex );
			Object value = (row != null)
				? row[columnIndex]
				: (columnIndex == 1 ? randomRowString( rowIndex ) : null);
			return (columnIndex == 0 && value == null) ? "item " + (rowIndex + 1) : value;
		}

		@Override
		public void setValueAt( Object value, int rowIndex, int columnIndex ) {
			if( rowIndex < rows.length )
				rows[rowIndex][columnIndex] = value;
			else {
				Object[] row = moreRowsMap.computeIfAbsent( rowIndex, k -> new Object[getColumnCount()] );
				row[columnIndex] = value;
			}

			fireTableCellUpdated( rowIndex, columnIndex );
		}
	}
}
