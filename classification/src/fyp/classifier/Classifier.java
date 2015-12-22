package fyp.classifier;

import java.util.List;
import java.util.ArrayList;
import fyp.utils.DatabaseConnector;
import fyp.utils.DatabaseAccess;
import fyp.models.ClassModel;
import java.sql.*;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;

public class Classifier{
    
    public Classifier(){

    }
    public void train(){
        List <String> subclasses = DatabaseAccess.getAllSubclasses();
        ClassModel model = null;
        for(String sb: subclasses){
            System.out.println("Processing subclass: " + sb);
            model = new ClassModel(sb);
            DatabaseAccess.buildWordMap(model);
            model.save();
            System.out.println("Word count: " + model.getCount());
            System.out.println("Vocabulary size: " + Vocabulary.vocabulary.size());
        }
        Vocabulary.save();
    }
    public void classify(String text){

    }
}