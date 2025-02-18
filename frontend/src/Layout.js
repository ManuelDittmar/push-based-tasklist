import React, { useState } from "react";
import {
    Header, HeaderName, HeaderGlobalBar, HeaderGlobalAction,
    SideNav, SideNavItems, SideNavMenuItem
} from "@carbon/react";
import { Menu, Close, User } from "@carbon/icons-react";
import { useNavigate } from "react-router-dom";

const sidebarWidth = 200; // Define sidebar width

const Layout = ({ children }) => {
    const [isSidebarOpen, setSidebarOpen] = useState(false);
    const navigate = useNavigate();

    return (
        <div style={{ display: "flex", flexDirection: "column", height: "100vh" }}>
            {/* Header */}
            <Header
                aria-label="Custom Tasklist"
                style={{
                    backgroundColor: "#f4f4f4",
                    height: "64px",
                    boxShadow: "0px 2px 5px rgba(0, 0, 0, 0.1)",
                    zIndex: 1000,
                    position: "fixed",
                    width: "100%",
                }}
            >
                <HeaderGlobalAction
                    aria-label="Toggle Sidebar"
                    onClick={() => setSidebarOpen(!isSidebarOpen)}
                >
                    {isSidebarOpen ? <Close size={24} /> : <Menu size={24} />}
                </HeaderGlobalAction>
                <HeaderName href="#" prefix="">
                    Custom Tasklist
                </HeaderName>
                <HeaderGlobalBar>
                    <HeaderGlobalAction aria-label="User Profile">
                        <User size={24} />
                    </HeaderGlobalAction>
                </HeaderGlobalBar>
            </Header>

            {/* Wrapper for Sidebar + Main Content */}
            <div style={{ display: "flex", flex: 1, marginTop: "64px" }}>
                {/* Sidebar Navigation */}
                <SideNav
                    isFixedNav
                    expanded={isSidebarOpen}
                    style={{
                        backgroundColor: "#f4f4f4",
                        boxShadow: "2px 0 5px rgba(0, 0, 0, 0.2)",
                        transition: "width 0.3s",
                        width: isSidebarOpen ? `${sidebarWidth}px` : "0",
                        overflow: "hidden",
                        position: "fixed",
                        height: "100vh",
                        zIndex: 900,
                    }}
                >
                    <SideNavItems>
                        <SideNavMenuItem
                            onClick={() => navigate("/tasks")}
                            style={{
                                padding: "10px 16px",
                                transition: "0.3s",
                                fontWeight: "bold",
                            }}
                            onMouseOver={(e) => (e.target.style.backgroundColor = "#e0e0e0")}
                            onMouseOut={(e) => (e.target.style.backgroundColor = "transparent")}
                        >
                            User Tasks
                        </SideNavMenuItem>
                    </SideNavItems>
                </SideNav>

                {/* Main Content - Moves Correctly */}
                <main
                    style={{
                        flex: 1,
                        marginLeft: isSidebarOpen ? `${sidebarWidth}px` : "0",
                        transition: "margin-left 0.3s",
                        height: "calc(100vh - 64px)",
                        overflowY: "auto",
                        backgroundColor: "#fff",
                    }}
                >
                    {children}
                </main>
            </div>
        </div>
    );
};

export default Layout;
