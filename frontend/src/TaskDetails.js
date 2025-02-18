import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {
    TextInput, Button, DatePicker, DatePickerInput
} from "@carbon/react";
import axios from "axios";

const TaskDetails = () => {
    const { taskId } = useParams();
    const [task, setTask] = useState(null);
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
                setFormData({
                    dueDate: response.data.dueDate ? new Date(response.data.dueDate) : null,
                    followUpDate: response.data.followUpDate ? new Date(response.data.followUpDate) : null,
                    assignee: response.data.assignee || "",
                    candidateUsers: response.data.candidateUsers || [],
                    candidateGroups: response.data.candidateGroups || [],
                    priority: response.data.priority ?? ""
                });
            })
            .catch(error => console.error("Error fetching task details:", error));
    }, [taskId]);

    const handleChange = (field, value) => {
        setFormData(prev => ({ ...prev, [field]: value }));
    };

    const cleanFormData = () => {
        const formatDate = (date) => {
            return date ? date.toISOString().split(".")[0] + "Z" : null;
        };

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
            .then(() => alert("Task updated successfully!"))
            .catch(error => console.error("Error updating task:", error));
    };

    if (!task) return <p>Loading...</p>;

    return (
        <div style={{ padding: "20px", maxWidth: "800px", margin: "auto" }}>
            <h2>{task.userTaskName}</h2>
            <p>Case: {task.customCaseId} / Task ID: {task.customUserTaskId}</p>

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "20px" }}>
                <TextInput id="createdAt" labelText="Created" value={task.createdAt} readOnly />
                <TextInput id="source" labelText="Source" value={task.source} readOnly />
                <TextInput id="processInstanceId" labelText="Instance ID" value={task.processInstanceId} readOnly />
                <TextInput id="taskState" labelText="Task State" value={task.taskState} readOnly />
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "20px" }}>
                <TextInput
                    id="assignee"
                    labelText="Assignee"
                    value={formData.assignee}
                    onChange={(e) => handleChange("assignee", e.target.value)}
                />
                <TextInput
                    id="priority"
                    labelText="Priority"
                    type="number"
                    value={formData.priority}
                    onChange={(e) => handleChange("priority", e.target.value)}
                />
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "20px" }}>
                <TextInput
                    id="candidateUsers"
                    labelText="Candidate Users"
                    value={formData.candidateUsers.join(", ")}
                    placeholder="Comma-separated"
                    onChange={(e) => handleChange("candidateUsers", e.target.value.split(",").map(u => u.trim()))}
                />
                <TextInput
                    id="candidateGroups"
                    labelText="Candidate Groups"
                    value={formData.candidateGroups.join(", ")}
                    placeholder="Comma-separated"
                    onChange={(e) => handleChange("candidateGroups", e.target.value.split(",").map(g => g.trim()))}
                />
            </div>

            <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: "10px", marginBottom: "20px" }}>
                <DatePicker
                    datePickerType="single"
                    value={formData.followUpDate ? [formData.followUpDate] : []}
                    onChange={(dates) => handleChange("followUpDate", dates.length > 0 ? dates[0] : null)}
                >
                    <DatePickerInput
                        id="followUpDate"
                        labelText="Follow-up Date"
                        placeholder="yyyy-mm-dd"
                        readOnly
                        style={{ width: "100%" }}
                    />
                </DatePicker>

                <DatePicker
                    datePickerType="single"
                    value={formData.dueDate ? [formData.dueDate] : []}
                    onChange={(dates) => handleChange("dueDate", dates.length > 0 ? dates[0] : null)}
                >
                    <DatePickerInput
                        id="dueDate"
                        labelText="Due Date"
                        placeholder="yyyy-mm-dd"
                        readOnly
                        style={{ width: "100%" }}
                    />
                </DatePicker>
            </div>

            <Button kind="primary" onClick={handleUpdate}>Update</Button>
        </div>
    );
};

export default TaskDetails;