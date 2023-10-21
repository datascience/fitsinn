import PropertyValueDistribution from "../PropertyValueDistribution";
import { Box, Typography, useTheme } from "@mui/material";
import { tokens } from "../../theme";
import Grid2 from "@mui/material/Unstable_Grid2";

const Histogram = ({ property }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  return (
    <Grid2 item sx="auto">
      <Box backgroundColor={colors.primary[400]}>
        <Typography
          variant="h5"
          fontWeight="600"
          sx={{ padding: "30px 30px 0 30px" }}
        >
          {property}
        </Typography>
        <Box>
          <PropertyValueDistribution property={property} />
        </Box>
      </Box>
    </Grid2>
  );
};

export default Histogram;
