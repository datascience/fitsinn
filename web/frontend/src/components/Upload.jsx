import { Box, useTheme } from "@mui/material";
import { tokens } from "../theme";
import Uppy from "@uppy/core";
import { Dashboard } from "@uppy/react";
import "@uppy/core/dist/style.css";
import "@uppy/dashboard/dist/style.css";
import XHRUpload from "@uppy/xhr-upload";
import { BACKEND_URL } from "../AppConfig";

const Upload = () => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  const uppy = new Uppy().use(XHRUpload, {
    endpoint: BACKEND_URL + "/upload",
  });
  return (
    <Box
      sx={{
        ".uppy-Dashboard-innerWrap, .uppy-Dashboard-AddFiles": {
          backgroundColor: colors.blueAccent[300],
          "border-style": "none",
        },
      }}
    >
      <Dashboard uppy={uppy} proudlyDisplayPoweredByUppy={false} />
    </Box>
  );
};

export default Upload;
