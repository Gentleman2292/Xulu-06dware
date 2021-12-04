package com.elementars.eclient.command;

import dev.xulu.settings.Value;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetBox extends JFrame
{
    JPanel jp = new JPanel();
    JLabel jl = new JLabel();
    JTextField jt = new JTextField(30);
    JButton jb = new JButton("Set");

    Value<String> setting;

    public SetBox(Value<String> setting) {
        this.setting = setting;
        setTitle("Setting");
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
                setting.setValue(input);
                //jl.setText(input);
            }
        });

        jp.add(jl);
        add(jp);
    }

    public static void initTextBox(Value<String> setting)
    {
        SetBox t = new SetBox(setting);
    }
}