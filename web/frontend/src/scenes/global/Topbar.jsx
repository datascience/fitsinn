import {
  Box,
  useTheme,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  IconButton,
} from "@mui/material";
import React, { useContext, useEffect, useState } from "react";
import InputLabel from "@mui/material/InputLabel";
import MenuItem from "@mui/material/MenuItem";
import FormControl from "@mui/material/FormControl";
import Select from "@mui/material/Select";
import { ColorModeContext, tokens } from "../../theme";

import Filter from "../../components/Filter";
import { BACKEND_URL } from "../../AppConfig";
import { useSessionStorage } from "@uidotdev/usehooks";

const Topbar = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);
  const [datasets, setDatasets] = useSessionStorage("datasets", []);

  const [dataset, setDataset] = useSessionStorage("dataset", "");

  // State for handling dialog
  const [openDialog, setOpenDialog] = useState(false);
  const [datasetToDelete, setDatasetToDelete] = useState("");

  var myHeaders = new Headers();
  myHeaders.append("Content-Type", "application/json");
  var requestGETOptions = {
    method: "GET",
    headers: myHeaders,
    redirect: "follow",
  };

  const fetchDatasets = async () => {
    try {
      const response = await fetch(
        BACKEND_URL + "/datasets",
        requestGETOptions
      );
      if (!response.ok) {
        throw new Error("Failed to fetch datasets");
      }
      const data = await response.json();
      setDatasets(data);
    } catch (error) {
      console.error(error);
    }
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

  const handleClick = (event) => {
    fetchDatasets();
  };

  // Open the delete confirmation dialog
  const handleDeleteClick = (e, item) => {
    // Prevent the menu from closing / triggering select
    e.stopPropagation();
    setDatasetToDelete(item);
    setOpenDialog(true);
  };

  // Close the delete dialog
  const handleCloseDialog = () => {
    setOpenDialog(false);
    setDatasetToDelete("");
  };

  // Send DELETE request to backend
  const handleConfirmDelete = async () => {
    if (!datasetToDelete) return;

    try {
      // Example: Assuming your backend supports a DELETE endpoint
      // such as /datasets/<datasetName>
      const deleteOptions = {
        method: "DELETE",
        headers: myHeaders,
        redirect: "follow",
      };

      const response = await fetch(
        `${BACKEND_URL}/datasets?` +
          new URLSearchParams({
            datasetName: dataset,
          }),
        deleteOptions
      );

      if (!response.ok) {
        throw new Error("Failed to delete dataset");
      }

      // Refetch datasets to update the UI
      await fetchDatasets();

      // If the currently selected dataset was just deleted, reset it
      if (dataset === datasetToDelete) {
        setDataset("");
      }
    } catch (error) {
      console.error(error);
    } finally {
      handleCloseDialog();
    }
  };

  return (
    <>
      <Box m="20px" display="flex" justifyContent="space-between">
        <Box>
          <Filter />
        </Box>
        <Box>
          <FormControl sx={{ minWidth: 120 }}>
            <InputLabel>Dataset</InputLabel>
            <Select
              label="Dataset"
              defaultValue={dataset}
              onClick={handleClick}
              onChange={handleChange}
            >
              {datasets.map((item) => (
                <MenuItem key={item} value={item}>
                  {item}
                  <IconButton
                    aria-label="delete"
                    size="small"
                    onClick={(e) => handleDeleteClick(e, item)}
                    sx={{ ml: 1 }} // margin-left for spacing
                  >
                    {" "}
                    x{" "}
                  </IconButton>
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>
      </Box>

      <Dialog open={openDialog} onClose={handleCloseDialog}>
        <DialogTitle>Delete Dataset</DialogTitle>
        <DialogContent>
          Are you sure you want to delete the dataset: {datasetToDelete} ?
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog}>Cancel</Button>
          <Button
            variant="contained"
            color="error"
            onClick={handleConfirmDelete}
          >
            Delete
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default Topbar;
