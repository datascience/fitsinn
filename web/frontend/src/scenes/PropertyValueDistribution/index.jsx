import SimpleBarChart from "../../components/SimpleBarChart";
import React, { useState, useEffect } from "react";
import { useTracked } from "react-tracked";
import { BACKEND_URL } from "../../AppConfig";

const PropertyValueDistribution = (payload) => {
  const [data, setData] = useState([]);
  const [state, dispatch] = useTracked();
  // GET with fetch API
  useEffect(() => {
    console.log("updating bar chart");
    var myHeaders = new Headers();
    myHeaders.append("Content-Type", "application/json");

    const fetchPost = async () => {
      try {
        var raw = JSON.stringify({
          property: payload["property"],
          filter: state.filter,
        });

        var requestOptions = {
          method: "POST",
          headers: myHeaders,
          body: raw,
          redirect: "follow",
        };

        const response = await fetch(
          BACKEND_URL +
            "/propertyvalues?" +
            new URLSearchParams({
              property: payload["property"],
              filter: state.filter,
            }),

          requestOptions
        );
        const data = await response.json();
        var sum = data.reduce(function (a, b, idx) {
          if (idx > 10) {
            return a + parseInt(b.count);
          } else {
            return 0;
          }
        });
        if (data.length > 10) {
          data.length = 10;
          data.push({ count: sum, value: "...others" });
        }
        data.reverse();
        setData(data);
      } catch (error) {
        console.log(error);
      }
    };
    fetchPost();
  }, [state.filter]);

  let filterClick = (property, event) => {
    if (event.indexValue == "...others") {
      return;
    }
    let newCondition = null;
    if (property == "FSLASTMODIFIED") {
      newCondition = `${property} >= "${event.indexValue}-01-01" && ${property} <= "${event.indexValue}-12-31"`;
    } else {
      newCondition = `${property} == "${event.indexValue}"`;
    }
    console.log("new condition: [" + newCondition + "]");
    console.log("Filter: [" + state.filter + "]");
    if (!state.filter.includes(newCondition)) {
      if (state.filter) {
        dispatch({
          filter: state.filter + " && " + newCondition,
        });
      } else {
        dispatch({
          filter: newCondition,
        });
      }
    }
  };

  return (
    <SimpleBarChart
      data={data}
      property={payload["property"]}
      filterClick={filterClick}
    />
  );
};

export default PropertyValueDistribution;
