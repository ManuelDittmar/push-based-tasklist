import { Form } from "@bpmn-io/form-js-viewer";
import React, { useEffect, useRef } from "react";
import axios from "axios";
import '@bpmn-io/form-js/dist/assets/form-js.css';

const FormViewer = ({ formSchema, inputContext, taskId, completedBy }) => {
    const formElement = useRef(null);
    const formInstance = useRef(null);

    useEffect(() => {
        if (!formElement.current) return;

        if (!formInstance.current) {
            formInstance.current = new Form();
            formInstance.current.attachTo(formElement.current);
            formInstance.current.importSchema(formSchema,inputContext);
        }

        formInstance.current.on("submit", async (event) => {
            event.preventDefault();

            try {
                await axios.post(`/api/tasks/${taskId}/complete`, {
                    ...event.data,
                    completedBy
                });
                console.log("✅ Task completed successfully!");
            } catch (error) {
                console.error("❌ Error completing task:", error.response?.data || error.message);
            }
        });

        return () => {
            if (formInstance.current) {
                formInstance.current.detach();
            }
        };
    }, [formSchema, inputContext, taskId, completedBy]);

    return <div ref={formElement} style={{ minHeight: "300px" }} />;
};

export default FormViewer;