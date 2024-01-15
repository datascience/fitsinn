import PropertyValueDistribution from "../PropertyValueDistribution";
import { Box, Typography, useTheme } from "@mui/material";
import { tokens } from "../../theme";
import Grid2 from "@mui/material/Unstable_Grid2";
import StatBox from "../../components/StatBox";
import EmailIcon from "@mui/icons-material/Email";
import CandlestickChartOutlinedIcon from "@mui/icons-material/CandlestickChartOutlined";
const Stat = ({ value, title }) => {
  const theme = useTheme();
  const colors = tokens(theme.palette.mode);

  return (
    <Grid2 item>
      <Box
        width={150}
        height={100}
        backgroundColor={colors.primary[400]}
        display="flex"
        alignItems="center"
        justifyContent="center"
      >
        <StatBox title={value} subtitle={title} />
      </Box>
    </Grid2>
  );
};

export default Stat;
