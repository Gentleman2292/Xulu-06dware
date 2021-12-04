package com.elementars.eclient.guirewrite.elements;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomeNotes extends JFrame
{
    JPanel jp = new JPanel();
    JLabel jl = new JLabel();
    JTextField jt = new JTextField(30);
    JButton jb = new JButton("Set Welcome Message (use NAME to mark the name)");

    public WelcomeNotes()
    {
        setTitle("Welcome Message");
        setVisible(true);
        setSize(400, 200);
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        jp.add(jt);


        jt.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String input = jt.getText();
                jl.setText(input);
            }
        });

        jp.add(jb);
        jb.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String input = jt.getText();
                Welcome.handleWelcome(input);
                //jl.setText(input);
            }
        });

        jp.add(jl);
        add(jp);
    }

    public static void initTextBox()
    {
        WelcomeNotes t = new WelcomeNotes();
    }
}