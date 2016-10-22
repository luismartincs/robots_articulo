package utils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author luismartin
 */
public class RobotMap {
    
    private int[][] map;
    private int rows;
    private int columns;
    
    public static final int FREE = 1;
    public static final int OBSTACLE = 2;
    public static final int GOAL = 3;

    
    public RobotMap(int row, int col){
        this.rows = row;
        this.columns = col;
        map = new int[row][col];
    }
    
    public void setInCell(int type,int row,int col){
        map[row][col] = type;
    }
    
    public int getDiscoveredSpace(){
        int count = 0;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(map[i][j] != 0){
                    count++;
                }
            }
        }      
        return count;
    }
    
    public void merge(RobotMap toMerge){
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(map[i][j] == 0){
                    map[i][j] = toMerge.get(i, j);
                }
            }
        }  
    }

    public void mergeMatrixAt(int [][]matrix, int row, int col){

        for (int i = -1; i < 1; ++i){
            for (int j = -1; j < 1; ++j){
                map[row+i][col+j] = matrix[1+i][1+j];
            }
        }

    }
    
    public void print(){
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.print("["+map[i][j]+"]");
            }
            System.out.println("");
        }
    }
    
    
    public static RobotMap getFromString(String protocol,int row,int col){
        
        RobotMap mapa = new RobotMap(row,col);
        
        String cells[] = protocol.split(",");
        
        int count = 0;
        
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                mapa.setInCell(Integer.parseInt(cells[count]),i,j);
                count++;
            }
        }
        
        return mapa;
    }

    @Override
    public String toString() {
        
        StringBuilder str = new StringBuilder();
        boolean first = true;
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if(first){
                    str.append(map[i][j]);
                    first = false;
                }else{
                    str.append(","+map[i][j]);
                }
            }
        }    
        return str.toString();
    }
    
    
    public int get(int row,int col){
        return map[row][col];
    }
    
    
    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

}
