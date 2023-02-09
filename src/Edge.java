public class Edge {
    private long beginNodeId;
    private long endNodeId;
    private double[][] speedMatrix;
    private double distance;

    public Edge(Long beginNodeId, Long endNodeId) {
        this.beginNodeId = beginNodeId;
        this.endNodeId = endNodeId;
    }

    public Edge(long beginNodeId, long endNodeId, double[][] speedMatrix, double distance) {
        this.beginNodeId = beginNodeId;
        this.endNodeId = endNodeId;
        this.speedMatrix = speedMatrix;
        this.distance = distance;
    }
    public Edge(long beginNodeId, long endNodeId, double distance) {
        this.beginNodeId = beginNodeId;
        this.endNodeId = endNodeId;
        this.distance = distance;
    }

    //nodeArrivelTime= time when we reach node
    //return the passed time from when reaching the node
    public int getTravelTime(int nodeArrivalTime) {
        int travelTime= 0;
        double time = (double) nodeArrivalTime;
        double distanceToGo = distance;
        double speed;

        //find Row
        double leftBorder= speedMatrix[0][0];
        double rightBorder= speedMatrix[1][0];
        int incrementor=0;
        while(!(leftBorder<=nodeArrivalTime) || !(nodeArrivalTime<rightBorder)){
            incrementor++;
            leftBorder = speedMatrix[0][incrementor];
            rightBorder = speedMatrix[1][incrementor];
        }
        int row = incrementor;
        speed = speedMatrix[2][row];


        double expectedArrivalTime = time + (distanceToGo/speed);

        while(expectedArrivalTime> speedMatrix[1][row]){
            distanceToGo = distanceToGo-(speed*(speedMatrix[1][row]-time));
            row++;
            time = speedMatrix[0][row];
            speed = speedMatrix[2][row];
            expectedArrivalTime = time + (distanceToGo/speed);
        }
        travelTime = (int) (expectedArrivalTime-nodeArrivalTime);
        return travelTime;
    }

    public Long getBeginNodeId() {
        return beginNodeId;
    }

    public void setBeginNodeId(Long beginNodeId) {
        this.beginNodeId = beginNodeId;
    }

    public Long getEndNodeId() {
        return endNodeId;
    }

    public void setEndNodeId(Long endNodeId) {
        this.endNodeId = endNodeId;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distanceM) {
        distance=distanceM;
    }
}