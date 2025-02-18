import React, { useEffect, useState } from "react";
import {
    Table, TableContainer, TableHead, TableRow, TableHeader,
    TableBody, TableCell, Pagination, TextInput, Button, Select, SelectItem
} from "@carbon/react";
import { Link } from "react-router-dom";
import axios from "axios";

const TaskList = () => {
    const [tasks, setTasks] = useState([]);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(10);
    const [totalItems, setTotalItems] = useState(0);
    const [customCaseId, setCustomCaseId] = useState("");
    const [source, setSource] = useState("");
    const [assignee, setAssignee] = useState("");
    const [taskState, setTaskState] = useState("");

    const hasActiveFilter = customCaseId || source || assignee || taskState;

    const cleanFilters = () => {
        const filters = { customCaseId, source, assignee, taskState };
        return Object.fromEntries(Object.entries(filters).filter(([_, value]) => value));
    };

    const fetchTasks = (filters = {}) => {
        axios.get(`/api/tasks`, {
            params: { page: page - 1, size, ...filters }
        })
            .then(response => {
                setTasks(response.data.content || []);
                setTotalItems(response.data.page?.totalElements ?? 0);
            })
            .catch(error => console.error("Error fetching tasks:", error));
    };

    useEffect(() => {
        fetchTasks(cleanFilters());
    }, [page, size]);

    const handleFilter = () => {
        setPage(1);
        fetchTasks(cleanFilters());
    };

    const handleCaseIdClick = (caseId) => {
        setCustomCaseId(caseId);
        setPage(1);
        fetchTasks({ customCaseId: caseId });
    };

    const resetFilters = () => {
        setCustomCaseId("");
        setSource("");
        setAssignee("");
        setTaskState("");
        setPage(1);
        fetchTasks();
    };

    const headers = [
        { key: "customCaseId", header: "Case ID" },
        { key: "customUserTaskId", header: "Task ID" },
        { key: "userTaskName", header: "Task Name" },
        { key: "priority", header: "Priority" },
        { key: "assignee", header: "Assignee" },
        { key: "source", header: "Source" }
    ];

    return (
        <div style={{
            display: "flex",
            flexDirection: "column",
            height: "calc(100vh - 64px)",
            width: "100%",
            padding: "0px",
            margin: "0px"
        }}>
            <TableContainer style={{ flex: 1, display: "flex", flexDirection: "column", padding: "0px", margin: "0px" }}>
                <div style={{ display: "flex", gap: "10px", padding: "16px", alignItems: "center" }}>
                    <TextInput
                        id="customCaseId"
                        labelText="Case ID"
                        placeholder="Enter Case ID"
                        value={customCaseId}
                        onChange={(e) => setCustomCaseId(e.target.value)}
                    />
                    <TextInput
                        id="source"
                        labelText="Source"
                        placeholder="Enter Source"
                        value={source}
                        onChange={(e) => setSource(e.target.value)}
                    />
                    <TextInput
                        id="assignee"
                        labelText="Assignee"
                        placeholder="Enter Assignee"
                        value={assignee}
                        onChange={(e) => setAssignee(e.target.value)}
                    />
                    <Select
                        id="taskState"
                        labelText="Task State"
                        value={taskState}
                        onChange={(e) => setTaskState(e.target.value)}
                    >
                        <SelectItem value="" text="All" />
                        <SelectItem value="CREATED" text="Created" />
                        <SelectItem value="COMPLETED" text="Completed" />
                        <SelectItem value="CANCELED" text="Canceled" />
                        <SelectItem value="FAILED" text="Failed" />
                    </Select>
                    <Button kind="primary" onClick={handleFilter}>Filter</Button>
                    {hasActiveFilter && (
                        <Button kind="secondary" onClick={resetFilters}>Reset Filters</Button>
                    )}
                </div>

                <div style={{
                    flex: 1,
                    overflowY: "auto",
                    height: "100%",
                    maxHeight: "calc(100vh - 180px)",
                    padding: "0px",
                    margin: "0px"
                }}>
                    <Table>
                        <TableHead>
                            <TableRow>
                                {headers.map(header => (
                                    <TableHeader key={header.key}>{header.header}</TableHeader>
                                ))}
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {tasks.length > 0 ? (
                                tasks.map(task => (
                                    <TableRow key={task.customUserTaskId}>
                                        <TableCell>
                                            <button
                                                onClick={() => handleCaseIdClick(task.customCaseId)}
                                                style={{
                                                    background: "none",
                                                    border: "none",
                                                    color: "#0f62fe",
                                                    cursor: "pointer",
                                                    textDecoration: "underline",
                                                    fontSize: "inherit"
                                                }}
                                            >
                                                {task.customCaseId}
                                            </button>
                                        </TableCell>
                                        <TableCell>
                                            <Link to={`/tasks/${task.customUserTaskId}`}>
                                                {task.customUserTaskId}
                                            </Link>
                                        </TableCell>
                                        <TableCell>{task.userTaskName}</TableCell>
                                        <TableCell>{task.priority}</TableCell>
                                        <TableCell>{task.assignee ?? "Unassigned"}</TableCell>
                                        <TableCell>{task.source ?? "N/A"}</TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={headers.length} style={{ textAlign: "center" }}>
                                        No tasks available
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </div>

                <div style={{
                    backgroundColor: "#fff",
                    position: "sticky",
                    bottom: "0px",
                    width: "100%",
                    padding: "0px",
                    margin: "0px",
                    boxShadow: "0px -2px 5px rgba(0, 0, 0, 0.1)"
                }}>
                    <Pagination
                        page={page}
                        pageSize={size}
                        totalItems={totalItems}
                        pageSizes={[5, 10, 20, 50]}
                        onChange={({ page, pageSize }) => {
                            setPage(page);
                            setSize(pageSize);
                        }}
                    />
                </div>
            </TableContainer>
        </div>
    );
};

export default TaskList;