import React, { useState } from "react";
import Snackbar from "@mui/material/Snackbar";
import { Alert, IconButton } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import { useSessionStorage } from "@uidotdev/usehooks";

const AlertMessage = ({ message }) => {
  const [open, setOpen] = useSessionStorage("alertMessage", true);
  function handleClose(event, reason) {
    if (reason === "clickaway") {
      return;
    }
    setOpen(false);
  }

  return (
    <div>
      <Snackbar
        anchorOrigin={{
          vertical: "bottom",
          horizontal: "right",
        }}
        open={open}
        autoHideDuration={5000}
        onClose={handleClose}
        ContentProps={{
          "aria-describedby": "message-id",
        }}
        action={[
          <IconButton key="close" onClick={handleClose}>
            <CloseIcon />
          </IconButton>,
        ]}
      >
        <Alert onClose={handleClose} severity="error">
          {message}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default AlertMessage;
