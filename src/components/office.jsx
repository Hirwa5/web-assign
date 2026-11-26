import React, { useState } from 'react'
import axios from 'axios'
import LibraryButton from './LibraryButton'

function Office() {

    const [officeName, setOfficeName] = useState("");
    const [officeNumber, setOfficeNumber] = useState("");
    const [deleteNumber, setDeleteNumber] = useState("");

    async function saveOffice() {
        try {
            const response = await axios.post("/api/office/save", {
                name: officeName,
                officeNumber: Number(officeNumber)
            });

            if (response.status == 200) {
                alert("Office saved successfully");
            } else {
                alert("Office is not saved");
            }
        } catch (error) {
            alert("This is the error: " + error.message + (error.response ? " - " + JSON.stringify(error.response.data) : ""));
        }
    }

    async function deleteOffice() {
        try {
            const allOffices = await axios.get("/api/office/all");
            const office = allOffices.data.find(o => o.officeNumber == Number(deleteNumber));

            if (office) {
                const response = await axios.delete("/api/office/delete/" + office.id);

                if (response.status == 200) {
                    alert("Office deleted successfully");
                } else {
                    alert("Office is not deleted");
                }
            } else {
                alert("Office not found");
            }
        } catch (error) {
            alert("This is the error: " + error.message + (error.response ? " - " + JSON.stringify(error.response.data) : ""));
        }
    }

    return (
        <div>
            <h1>SAVE OFFICE</h1>
            <label>OFFICE NAME : </label>
            <input type="text" onChange={e => setOfficeName(e.target.value)} /><br />
            <label>OFFICE NUMBER : </label>
            <input type="text" onChange={e => setOfficeNumber(e.target.value)} /><br />
            <LibraryButton buttonAction={saveOffice} buttonLabel="SAVE" />

            <h1>DELETE OFFICE</h1>
            <label>OFFICE NUMBER : </label>
            <input type="text" onChange={e => setDeleteNumber(e.target.value)} /><br />
            <LibraryButton buttonAction={deleteOffice} buttonLabel="DELETE" />
        </div>
    )
}

export default Office
