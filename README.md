# cs311PA2
https://canvas.iastate.edu/courses/55851/assignments/804772

Randomized Test Cases:

    public static void main(String[] args){
        CommunicationsMonitor cm = new CommunicationsMonitor();
        /*cm.addCommunication(1, 2, 3);
        cm.addCommunication(1, 3, 4);
        cm.addCommunication(2, 5, 6);
        cm.addCommunication(3, 5, 8);*/
        Random r = new Random();
        for(int i = 0; i < 100; i++){
            int c1 = r.nextInt(15) + 1;
            int c2 = c1;
            while(c2 == c1) c2 = r.nextInt(15) + 1;
            int timestamp = r.nextInt(40) + 1;
            cm.addCommunication(c1, c2, timestamp);
        }
        cm.createGraph();
        List<ComputerNode> l;
        for(int i = 0; i < 100; i++) {
            int c1 = r.nextInt(15) + 1;
            //int c2 = c1;
            //while(c2 == c1) c2 = r.nextInt(15) + 1;
            int c2 = r.nextInt(15) + 1;
            System.out.println("Query " + i + 1 + ": " + c1 + ", " + c2);
            l = cm.queryInfection(c1, c2, 1, 40);
            if(l != null) {
                for (ComputerNode c : l) {
                    System.out.println(c.ID + ", " + c.getTimestamp());
                }
            }
            System.out.println();
        }
        System.out.println(cm.queryInfection(100, 1000, 1, 100));
        System.out.println("No errors!");
    }