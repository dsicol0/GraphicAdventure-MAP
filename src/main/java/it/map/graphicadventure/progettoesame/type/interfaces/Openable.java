/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.type.interfaces;

/**
 *
 * @author David
 */
public interface Openable {
    
    void open();
    void close();
    boolean isOpen();

    boolean isLocked();
    void setOpen(boolean locked);
}
