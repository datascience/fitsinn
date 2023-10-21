import { Box, useTheme } from "@mui/material";
import Grid2 from "@mui/material/Unstable_Grid2";
import { tokens } from "../../theme";
import Header from "../../components/Header";
import React, { useState, useEffect } from "react";
import { BACKEND_URL } from "../../AppConfig";
import Histogram from "./histogram";
import Stat from "./stat";

const Dashboard = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [sizeStatistics, setSizeStatistics] = useState({
    totalSize: 10047,
    avgSize: 3349,
    sizeDistribution: [
      { count: 2, value: "0-1KB" },
      { count: 1, value: "1KB-1MB" },
    ],
    minSize: 4,
    maxSize: 10000,
  });

  useEffect(() => {
    console.log("loading the dashboard");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const fetchPost = async () => {
      try {
        var requestOptions = {
          method: "GET",
          headers: myHeaders,
          redirect: "follow",
        };

        const response = await fetch(
          BACKEND_URL + "/statistics",
          requestOptions
        );
        const data = await response.json();

        setSizeStatistics(data);
      } catch (error) {
        console.log(error);
      }
    };
    fetchPost();
  }, []);

  return (
    <Box m="20px">
      {/* HEADER */}
      <Box display="flex" justifyContent="space-between" alignItems="center">
        <Header title="Dashboard" subtitle="Collection Overview" />
      </Box>

      {/* GRID & CHARTS */}
      <Grid2 container spacing={1} sx={4}>
        <Stat
          title="Total File Count"
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
      </Grid2>
      <Grid2 container spacing={1}>
        <Histogram property="MIMETYPE"></Histogram>
        <Histogram property="FORMAT"></Histogram>
        <Histogram property="EXTERNALIDENTIFIER"></Histogram>

        <Histogram property="FORMAT_VERSION"></Histogram>
        <Histogram property="FSLASTMODIFIED"></Histogram>
        <Histogram property="SIZE"></Histogram>
      </Grid2>
    </Box>
  );
};

export default Dashboard;
