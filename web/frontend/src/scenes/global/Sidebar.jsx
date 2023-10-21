import { useState } from "react";
import {
  ProSidebar,
  Menu,
  MenuItem,
  SidebarHeader,
  SidebarFooter,
  SidebarContent,
} from "react-pro-sidebar";
import { Box, IconButton, Typography, useTheme } from "@mui/material";
import { Link } from "react-router-dom";
import "react-pro-sidebar/dist/css/styles.css";
import { ColorModeContext, tokens } from "../../theme";
import ManageSearchOutlinedIcon from "@mui/icons-material/ManageSearchOutlined";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import FileUploadOutlinedIcon from "@mui/icons-material/FileUploadOutlined";
import DashboardOutlinedIcon from "@mui/icons-material/DashboardOutlined";
import FormatListBulletedOutlinedIcon from "@mui/icons-material/FormatListBulletedOutlined";
import QuizOutlinedIcon from "@mui/icons-material/QuizOutlined";
import { useContext } from "react";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";

const Item = ({ title, to, icon, selected, setSelected }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  return (
    <MenuItem
      active={selected === title}
      style={{
        color: colors.grey[100],
      }}
      onClick={() => setSelected(title)}
      icon={icon}
    >
      <Typography>{title}</Typography>
      <Link to={to} />
    </MenuItem>
  );
};

const Sidebar = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const colorMode = useContext(ColorModeContext);
  const [isCollapsed, setIsCollapsed] = useState(false);
  const [selected, setSelected] = useState("Dashboard");

  return (
    <Box
      sx={{
        "& .pro-sidebar-inner": {
          background: `${colors.primary[400]} !important`,
        },
        "& .pro-icon-wrapper": {
          backgroundColor: "transparent !important",
        },
        "& .pro-inner-item": {
          padding: "5px 35px 5px 20px !important",
        },
        "& .pro-inner-item:hover": {
          color: "#868dfb !important",
        },
        "& .pro-menu-item.active": {
          color: "#6870fa !important",
        },
      }}
    >
      <ProSidebar collapsed={isCollapsed}>
        <Menu iconShape="square">
          <SidebarHeader>
            {/* LOGO AND MENU ICON */}
            <MenuItem
              onClick={() => setIsCollapsed(!isCollapsed)}
              icon={isCollapsed ? <MenuOutlinedIcon /> : undefined}
              style={{
                margin: "10px 0 20px 0",
                color: colors.grey[100],
              }}
            >
              {!isCollapsed && (
                <Box
                  display="flex"
                  justifyContent="space-between"
                  alignItems="center"
                  ml="15px"
                >
                  <Typography variant="h3" color={colors.grey[100]}>
                    Navigation
                  </Typography>

                  <IconButton onClick={() => setIsCollapsed(!isCollapsed)}>
                    <MenuOutlinedIcon />
                  </IconButton>
                </Box>
              )}
            </MenuItem>
          </SidebarHeader>
          <SidebarContent>
            {!isCollapsed && (
              <Box mb="25px">
                <Box display="flex" justifyContent="center" alignItems="center">
                  <img
                    alt="profile-user"
                    width="100px"
                    height="100px"
                    src={`../../assets/logo.png`}
                    style={{ cursor: "pointer", borderRadius: "50%" }}
                  />
                </Box>
                <Box textAlign="center">
                  <Typography
                    variant="h2"
                    color={colors.grey[100]}
                    fontWeight="bold"
                    sx={{ m: "10px 0 0 0" }}
                  >
                    FITSInn
                  </Typography>
                  <Typography variant="h5" color={colors.greenAccent[500]}>
                    Learn Your Digital Assets
                  </Typography>
                </Box>
              </Box>
            )}

            <Box paddingLeft={isCollapsed ? undefined : "10%"}>
              <Item
                title="Dashboard"
                to="/"
                icon={<DashboardOutlinedIcon />}
                selected={selected}
                setSelected={setSelected}
              />

              <Item
                title="Digital Objects"
                to="/objects"
                icon={<FormatListBulletedOutlinedIcon />}
                selected={selected}
                setSelected={setSelected}
              />

              <Item
                title="Samples"
                to="/samples"
                icon={<ManageSearchOutlinedIcon />}
                selected={selected}
                setSelected={setSelected}
              />

              <Item
                title="Upload Data"
                to="/upload"
                icon={<FileUploadOutlinedIcon />}
                selected={selected}
                setSelected={setSelected}
              />

              <Item
                title="Help"
                to="/faq"
                icon={<QuizOutlinedIcon />}
                selected={selected}
                setSelected={setSelected}
              />
            </Box>
          </SidebarContent>

          <SidebarFooter>
            <Box paddingLeft={isCollapsed ? undefined : "10%"}>
              <MenuItem
                style={{
                  color: colors.grey[100],
                }}
                onClick={colorMode.toggleColorMode}
                icon={
                  theme.palette.mode === "dark" ? (
                    <DarkModeOutlinedIcon />
                  ) : (
                    <LightModeOutlinedIcon />
                  )
                }
              >
                <Typography>Dark Mode</Typography>
              </MenuItem>
            </Box>
          </SidebarFooter>
        </Menu>
      </ProSidebar>
    </Box>
  );
};

export default Sidebar;
