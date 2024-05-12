import {Box, Button, IconButton, Typography, useTheme} from "@mui/material";
import React, {useContext, useEffect, useState} from "react";
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import { ColorModeContext, tokens } from "../../theme";

import Filter, {uniqueProperties} from "../../components/Filter";
import {BACKEND_URL} from "../../AppConfig";
import {useSessionStorage} from "@uidotdev/usehooks";
import Histogram from "../dashboard/histogram";
const Topbar = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
    const [datasets, setDatasets] = useSessionStorage(
        "datasets",
        []
    );

    const [dataset, setDataset] = useSessionStorage(
        "dataset",
        ""
    );

    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");
    var requestGETOptions = {
        method: "GET",
        headers: myHeaders,
        redirect: "follow",
    };

    const fetchDatasets = async () => {
        const response = await fetch(BACKEND_URL + "/datasets", requestGETOptions);
        let data = await response.json();
        setDatasets(data);
    };



  const colorMode = useContext(ColorModeContext);

    const fetchData = async () => {
        await fetchDatasets();
    };

    useEffect(() => {
        fetchDatasets();
    }, []);



    const handleChange = (event) => {
        setDataset(event.target.value);
    };



    return (
      <Box  m="20px" display="flex" justifyContent="space-between">
        <Box>
          <Filter />
        </Box>
        <Box>
          <FormControl sx={{minWidth: 120 }}>
            <InputLabel>Dataset</InputLabel>
            <Select label="Dataset" defaultValue={dataset} onChange={handleChange}>
                {datasets.map((item) => {
                   return  <MenuItem value={item}>{item}</MenuItem>
                })}
            </Select>
          </FormControl>
        </Box>
      </Box>
  );
};

export default Topbar;
