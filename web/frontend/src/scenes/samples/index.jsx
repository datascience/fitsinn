import { Box } from "@mui/material";
import Header from "../../components/Header";
import Table from "../../components/Table";
import React, { useState, useEffect } from "react";
import { BACKEND_URL } from "../../AppConfig";
import { useNavigate } from "react-router-dom";

import { useSessionStorage } from "@uidotdev/usehooks";

const Samples = () => {
  const [filter, setFilter] = useSessionStorage("filterString", "");

  const [selectedObject, setSelectedObject] = useSessionStorage(
    "selectedObject",
    ""
  );

  const [data, setData] = useState([
    {
      id: 1,
      filepath: "/usr/local/tomcat/webapps/fits/upload/1582118786085/README.md",
    },
  ]);
  const navigate = useNavigate();
  useEffect(() => {
    console.log("loading the object list");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const fetchPost = async () => {
      try {
        var raw = JSON.stringify({
          filter: filter,
        });

        var requestOptions = {
          method: "POST",
          headers: myHeaders,
          body: raw,
          redirect: "follow",
        };

        const response = await fetch(
          BACKEND_URL +
            "/samples?" +
            new URLSearchParams({
              filter: filter,
              properties: "FORMAT",
              properties: "MIMETYPE",
              algorithm: "SELECTIVE_FEATURE_DISTRIBUTION",
            }),
          requestOptions
        );
        const data = await response.json();

        var tmp = [];
        data.forEach(function (item, index) {
          tmp.push({ id: index, filepath: item });
        });
        setData(tmp);
      } catch (error) {
        console.log(error);
      }
    };
    fetchPost();
  }, [filter]);

  const initialState = {
    sorting: {
      sortModel: [{ field: "filepath", sort: "asc" }],
    },
  };

  const columns = [
    {
      field: "filepath",
      headerName: "File Path",
      flex: 0.5,
    },
  ];

  const handleRowClick = (params) => {
    console.log(params.row.filepath);
    setSelectedObject(params.row.filepath);
    navigate(`/objectdetails`);
  };

  return (
    <Box m="20px">
      <Header
        title="Samples"
        subtitle="The result of the selected sampling algorithm"
      ></Header>
      <Box height="75vh">
        <Table
          data={data}
          columns={columns}
          initialState={initialState}
          onRowClick={handleRowClick}
        />
      </Box>
    </Box>
  );
};

export default Samples;
