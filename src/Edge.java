public class Edge {
    private Long beginNodeId;
    private Long endNodeId;
    private Double[][] speedMatrix;
    private Double distance;

    public Edge(Long beginNodeId, Long endNodeId) {
        this.beginNodeId = beginNodeId;
        this.endNodeId = endNodeId;
    }

    public Edge(Long beginNodeId, Long endNodeId, Double[][] speedMatrix, Double distance) {
        this.beginNodeId = beginNodeId;
        this.endNodeId = endNodeId;
        this.speedMatrix = speedMatrix;
        this.distance = distance;
    }

    //nodeArrivelTime= time when we reach node
    //return the passed time from when reaching the node
    public int getTravelTime(int nodeArrivalTime) {
        int travelTime= 0;
        Double time = (double) nodeArrivalTime;
        double distanceToGo = distance;
        double speed;

        //find Row
        Double leftBorder= speedMatrix[0][0];
        Double rightBorder= speedMatrix[1][0];
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

    public void calculateDistance() {

    }

    public void setDistance(double distanceM) {
        distance=distanceM;
    }
}