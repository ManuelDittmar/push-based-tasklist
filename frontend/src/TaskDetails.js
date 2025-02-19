import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
    TextInput, Button, DatePicker, DatePickerInput,
    Table, TableContainer, TableHead, TableRow, TableHeader,
    TableBody, TableCell, Pagination
} from "@carbon/react";
import axios from "axios";
import FormViewer from "./FormViewer";

const TaskDetails = () => {
    const { taskId } = useParams();
    const [task, setTask] = useState(null);
    const [taskHistory, setTaskHistory] = useState([]);
    const [formSchema, setFormSchema] = useState(null);
    const [page, setPage] = useState(1);
    const [size, setSize] = useState(10);
    const [totalItems, setTotalItems] = useState(0);
    const [showHistory, setShowHistory] = useState(false);

    const [formData, setFormData] = useState({
        dueDate: null,
        followUpDate: null,
        assignee: "",
        candidateUsers: [],
        candidateGroups: [],
        priority: ""
    });

    useEffect(() => {
        axios.get(`/api/tasks/${taskId}`)
            .then(response => {
                setTask(response.data);
                console.log("✅ Task loaded:", response.data);

                setFormData({
                    dueDate: response.data.dueDate ? new Date(response.data.dueDate) : null,
                    followUpDate: response.data.followUpDate ? new Date(response.data.followUpDate) : null,
                    assignee: response.data.assignee || "",
                    candidateUsers: response.data.candidateUsers || [],
                    candidateGroups: response.data.candidateGroups || [],
                    priority: response.data.priority ?? ""
                });

                // Load the form schema from the public folder
                const formPath = "/forms/SubmitPaper.form.json";
                console.log("🚀 Fetching hardcoded form from:", formPath);

                axios.get(formPath)
                    .then(res => {
                        console.log("✅ Form schema loaded:", res.data);
                        setFormSchema(res.data);
                    })
                    .catch(err => console.error("❌ Error loading form schema:", err));
            })
            .catch(error => console.error("❌ Error fetching task details:", error));
    }, [taskId]);

    useEffect(() => {
        if (showHistory) {
            axios.get(`/api/tasks/${taskId}/history`, {
                params: { page: page - 1, size }
            })
                .then(response => {
                    setTaskHistory(response.data.content || []);
                    setTotalItems(response.data.page?.totalElements ?? 0);
                })
                .catch(error => console.error("Error fetching task history:", error));
        }
    }, [taskId, page, size, showHistory]);

    const handleChange = (field, value) => {
        setFormData(prev => ({ ...prev, [field]: value }));
    };

    const cleanFormData = () => {
        const formatDate = (date) => date ? date.toISOString().split(".")[0] + "Z" : null;

        return {
            ...formData,
            candidateUsers: formData.candidateUsers.length ? formData.candidateUsers : [],
            candidateGroups: formData.candidateGroups.length ? formData.candidateGroups : [],
            dueDate: formatDate(formData.dueDate),
            followUpDate: formatDate(formData.followUpDate),
        };
    };

    const handleUpdate = () => {
        const payload = cleanFormData();

        axios.put(`/api/tasks/${taskId}`, payload)
            .then(() => console.log("Task updated successfully!"))
            .catch(error => console.error("Error updating task:", error));
    };

    if (!task) return <p>Loading...</p>;

    return (
        <div style={{
            display: "flex",
            flexDirection: "column",
            gap: "20px",
            margin: "auto",
            width: "100%",
            overflow: "hidden",
            minHeight: "100vh"
        }}>
            <div style={{
                flex: 1,
                display: "grid",
                gridTemplateColumns: "3fr 0.1fr 1fr",
                gap: "20px",
                width: "100%",
                alignItems: "flex-start"
            }}>
                <div style={{ display: "flex", flexDirection: "column", gap: "10px", padding: "20px" }}>
                    <h4>{task.userTaskName}</h4>
                    <p>Case: {task.customCaseId} / Task ID: {task.customUserTaskId}</p>

                    <div style={{
                        display: "grid",
                        gridTemplateColumns: "1fr 1fr",
                        gap: "10px"
                    }}>
                        <TextInput id="createdAt" labelText="Created" value={task.createdAt} readOnly style={{ width: "100%" }} />
                        <TextInput id="source" labelText="Source" value={task.source} readOnly style={{ width: "100%" }} />
                        <TextInput id="processInstanceId" labelText="Instance ID" value={task.processInstanceId} readOnly style={{ width: "100%" }} />
                        <TextInput id="taskState" labelText="Task State" value={task.taskState} readOnly style={{ width: "100%" }} />
                    </div>

                    {formSchema && (
                        <FormViewer
                            formSchema={formSchema}
                            inputContext={task.variables || {}}
                            taskId={taskId}
                            completedBy={formData.assignee}
                        />
                    )}

                </div>

                <div style={{
                    backgroundColor: "#ccc",
                    width: "2px",
                    height: "100%"
                }} />

                <div style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: "16px",
                    justifyContent: "flex-start",
                    alignItems: "stretch",
                    padding: "20px",
                    width: "100%"
                }}>
                    <TextInput id="assignee" labelText="Assignee" value={formData.assignee} onChange={(e) => handleChange("assignee", e.target.value)} style={{ width: "100%" }} />
                    <TextInput id="priority" labelText="Priority" type="number" value={formData.priority} onChange={(e) => handleChange("priority", e.target.value)} style={{ width: "100%" }} />
                    <TextInput id="candidateUsers" labelText="Candidate Users" value={formData.candidateUsers.join(", ")} placeholder="Comma-separated" onChange={(e) => handleChange("candidateUsers", e.target.value.split(",").map(u => u.trim()))} style={{ width: "100%" }} />
                    <TextInput id="candidateGroups" labelText="Candidate Groups" value={formData.candidateGroups.join(", ")} placeholder="Comma-separated" onChange={(e) => handleChange("candidateGroups", e.target.value.split(",").map(g => g.trim()))} style={{ width: "100%" }} />

                    <div style={{ display: "flex", gap: "10px", width: "100%" }}>
                        <DatePicker datePickerType="single" onChange={(dates) => handleChange("followUpDate", dates.length > 0 ? dates[0] : null)}>
                            <DatePickerInput id="followUpDate" labelText="Follow-up Date" placeholder="yyyy-mm-dd" readOnly style={{ width: "100%" }} />
                        </DatePicker>

                        <DatePicker datePickerType="single" onChange={(dates) => handleChange("dueDate", dates.length > 0 ? dates[0] : null)}>
                            <DatePickerInput id="dueDate" labelText="Due Date" placeholder="yyyy-mm-dd" readOnly style={{ width: "100%" }} />
                        </DatePicker>
                    </div>

                    <Button kind="primary" onClick={handleUpdate} style={{ width: "100%" }}>Update</Button>
                </div>
            </div>

            <Button kind="tertiary" onClick={() => setShowHistory(prev => !prev)} style={{ width: "200px", alignSelf: "flex-start" }}>
                {showHistory ? "Hide History" : "Show History"}
            </Button>

            {showHistory && (
                <div style={{ width: "100%", maxHeight: "30vh", overflowY: "auto" }}>
                    <TableContainer>
                        <Table>
                            <TableHead style={{ position: "sticky", top: "0", backgroundColor: "white", zIndex: 1 }}>
                                <TableRow>
                                    <TableHeader>Timestamp</TableHeader>
                                    <TableHeader>Event Type</TableHeader>
                                    <TableHeader>Field Name</TableHeader>
                                    <TableHeader>From</TableHeader>
                                    <TableHeader>To</TableHeader>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {taskHistory.map(entry => (
                                    <TableRow key={entry.id}>
                                        <TableCell>{entry.timestamp}</TableCell>
                                        <TableCell>{entry.eventType}</TableCell>
                                        <TableCell>{entry.fieldName || "-"}</TableCell>
                                        <TableCell>{entry.fromValue || "-"}</TableCell>
                                        <TableCell>{entry.toValue || "-"}</TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </div>
            )}
        </div>
    );
};

export default TaskDetails;