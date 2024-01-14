import { Box, Button, Typography, useTheme } from "@mui/material";
import Grid2 from "@mui/material/Unstable_Grid2";
import { useSessionStorage } from "@uidotdev/usehooks";
import React, { useEffect, useState } from "react";
import { BACKEND_URL } from "../../AppConfig";
import Header from "../../components/Header";
import StatBox from "../../components/StatBox";
import { tokens } from "../../theme";
import Histogram from "./histogram";
import Stat from "./stat";

const Dashboard = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [sizeStatistics, setSizeStatistics] = useState([
    {
      totalSize: 10047,
      avgSize: 3349,
      minSize: 4,
      maxSize: 10000,
      conflictRate: 0.17,
    },
  ]);

  const [properties, setProperties] = useState([]);

  const [filter, setFilter] = useSessionStorage("filterString", "");

  const [globalProperties, setGlobalProperties] = useSessionStorage(
    "globalProperties",
    []
  );
  const [conflictResolution, setConflictResolution] = useSessionStorage(
    "conflictResolution",
    {
      color: colors.blueAccent[700],
      text: "resolve",
    }
  );

  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");

  const fetchStatistics = async () => {
    var requestOptions = {
      method: "POST",
      headers: myHeaders,
      redirect: "follow",
    };
    const response = await fetch(
      BACKEND_URL +
        "/statistics?" +
        new URLSearchParams({
          filter: filter,
        }),
      requestOptions
    );
    const data = await response.json();
    setSizeStatistics(data);
  };

  useEffect(() => {
    console.log("loading the dashboard");

    fetchStatistics();
  }, [filter]);

  const handleClick = () => {
    console.log("Conflict resolution started");
    const fetchPost = async () => {
      var requestOptions = {
        method: "POST",
        headers: myHeaders,
        redirect: "follow",
      };
      const response = await fetch(
        BACKEND_URL + "/resolveconflicts",
        requestOptions
      );
      setConflictResolution({
        color: colors.blueAccent[700],
        text: "resolved",
      });
      console.log("Conflict resolution finished");
    };
    fetchPost();
    setConflictResolution({
      color: colors.blueAccent[300],
      text: "resolving",
    });
  };

  return (
    <Box m="20px">
      {/* HEADER */}
      <Box display="flex" justifyContent="space-between" alignItems="center">
        <Header title="Dashboard" subtitle="Collection Overview" />
      </Box>

      {/* GRID & CHARTS */}
      <Grid2 container spacing={1}>
        <Stat
          title="File Count"
          value={
            sizeStatistics.totalCount == null ? 0 : sizeStatistics.totalCount
          }
        />

        <Stat
          title="Total Size (MB)"
          value={
            sizeStatistics.totalSize == null
              ? 0
              : (sizeStatistics.totalSize / 1024 / 1024).toFixed(2)
          }
        />

        <Stat
          title="Average File Size (MB)"
          value={
            sizeStatistics.avgSize == null
              ? 0
              : (sizeStatistics.avgSize / 1024 / 1024).toFixed(2)
          }
        />

        <Stat
          title="Smallest File Size (MB)"
          value={
            sizeStatistics.minSize == null
              ? 0
              : (sizeStatistics.minSize / 1024 / 1024).toFixed(2)
          }
        />

        <Stat
          title="Biggest File Size (MB)"
          value={
            sizeStatistics.maxSize == null
              ? 0
              : (sizeStatistics.maxSize / 1024 / 1024).toFixed(2)
          }
        />

        <Grid2 item>
          <Box
            width={210}
            height={100}
            backgroundColor={colors.primary[400]}
            display="flex"
            alignItems="center"
            justifyContent="center"
          >
            <StatBox
              subtitle="Conflct Rate (%)"
              title={
                sizeStatistics.conflictRate == null
                  ? 0
                  : (sizeStatistics.conflictRate * 100).toFixed(2)
              }
            />
            <Box
              sx={{
                ".MuiButton-root": {
                  color: colors.grey[100],
                  backgroundColor: colors.blueAccent[700],
                  fontSize: 12,
                  margin: "0px 20px 0px -20px",
                  width: 90,
                },
              }}
            >
              <Button onClick={handleClick}>
                <Typography variant="h5" fontWeight="600">
                  {conflictResolution.text}
                </Typography>
              </Button>
            </Box>
          </Box>
        </Grid2>
      </Grid2>
      <Grid2 container spacing={1}>
        {globalProperties.map((prop) => (
          <Histogram property={prop}></Histogram>
        ))}
      </Grid2>
    </Box>
  );
};

export default Dashboard;
