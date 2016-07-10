# Coding Summary
### Data Structures
- ```Map <edge,Date> liveEdges``` - It keeps track of each edge with timestamp.

- ```Map <String,Integer> userConnect``` - Keeps track of total number of edges connected with each node.

### Methods
- ```collectData()``` -
    It fetches transaction objects from given input file and send it to processData() method.
- ```processData()``` -
 On recieving transaction object, it retrives values from objects. Now it compares timestamp of object with recent maximum timestamp and if new timestamp is greater than maximum timestamp then it will update maximum timestamp and  call removeOld() to remove edges which are older than sixty second of window. If timestamp of object is between than maximum timestamp and sixty second window, then it puts edge into liveEdges.

- ```removeOld()``` - It iterates through liveEdges and remove all edges those have timestamp less than current sixty second window and update graph.

- ```updateCount()``` - It increments total numbers of connected edges for given node.

- ```deleteCount()``` - It decrements total numbers of connected edges for given node while removing edges from liveEdges. If node becomes disconnected then also removes node from userConnect. 

- ```getMedian()``` - After updating recent graph, it will retrive total number of connected edges for each node and calculate median of graph and  write into output file.

### Dependency
java-json.jar (Included into  **src/** folder)