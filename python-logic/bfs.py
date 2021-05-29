from collections import defaultdict


# Function to build the graph
def build_graph(vertical, horizontal):
    graph = defaultdict(list)

    for y in range(9):
        for x in range(8):
            if y > 0:
                if vertical[y][x] == 0 and vertical[y - 1][x] == 0:
                    graph[(y) * 10 + (x)].append((y) * 10 + x + 1)
                    graph[(y) * 10 + x + 1].append((y) * 10 + (x))
            else:
                graph[(y) * 10 + (x)].append((y) * 10 + x + 1)
                graph[(y) * 10 + x + 1].append((y) * 10 + (x))


    for y in range(8):
        for x in range(9):
            if x > 0:
                if horizontal[y][x] == 0 and horizontal[y][x - 1] == 0:
                    graph[y * 10 + x].append((y + 1) * 10 + x)
                    graph[(y + 1) * 10 + x].append(y * 10 + x)
            else:
                graph[y * 10 + x].append((y + 1) * 10 + x)
                graph[(y + 1) * 10 + x].append(y * 10 + x)

    return graph

def convert_goal(goal):
    return_value = []
    for g in goal:
        return_value.append(g["y"] * 10 + g["x"])
    return return_value

def BFS_SP(graph, start, goal):
        start = start[0] * 10 + start[1]
        goal = convert_goal(goal)
        explored = []

        # Queue for traversing the
        # graph in the BFS
        queue = [[start]]

        # If the desired node is
        # reached
        if goal.count(start) > 0:
            print("Same Node")
            return

        # Loop to traverse the graph
        # with the help of the queue
        while queue:
            path = queue.pop(0)
            node = path[-1]

            # Condition to check if the
            # current node is not visited
            if node not in explored:
                neighbours = graph[node]

                # Loop to iterate over the
                # neighbours of the node
                for neighbour in neighbours:
                    new_path = list(path)
                    new_path.append(neighbour)
                    queue.append(new_path)

                    # Condition to check if the
                    # neighbour node is the goal
                    if goal.count(neighbour) > 0:
                        #print("Shortest path = ", *new_path)
                        return len(new_path)
                explored.append(node)

        # Condition when the nodes
        # are not connected
        print("So sorry, but a connecting" \
              "path doesn't exist :(")
        return 100
